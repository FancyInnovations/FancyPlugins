package com.fancyinnovations.fancyworlds.backups;

import com.google.gson.Gson;
import org.bukkit.World;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * File format and path checks for FancyWorlds backup archives.
 */
public final class BackupArchive {

    private static final Gson GSON = new Gson();
    private static final String MANIFEST_ENTRY = "manifest.json";
    private static final String WORLD_PREFIX = "world/";

    private BackupArchive() {
    }

    public static boolean validName(String name) {
        return name != null && name.matches("[A-Za-z0-9_-]{1,64}");
    }

    public static boolean validBackupId(String id) {
        if (id == null) {
            return false;
        }
        if (id.matches("FW_[A-Za-z0-9_-]{1,64}_\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}-\\d{2}(?:_\\d+)?")) {
            return true;
        }

        try {
            return UUID.fromString(id).toString().equals(id);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public static Path safeRelative(String value) throws IOException {
        if (value == null || value.isBlank() || value.contains("\\") || value.contains("//") || value.startsWith("/") || value.endsWith("/")) {
            throw new IOException("Unsafe path");
        }

        Path path;
        try {
            path = Path.of(value);
        } catch (InvalidPathException ex) {
            throw new IOException("Unsafe path", ex);
        }

        if (path.isAbsolute()) {
            throw new IOException("Unsafe path");
        }

        for (Path part : path) {
            if (part.toString().isBlank() || part.toString().equals(".") || part.toString().equals("..")) {
                throw new IOException("Unsafe path");
            }
        }

        if (!path.normalize().equals(path)) {
            throw new IOException("Unsafe path");
        }

        return path;
    }

    public static void rejectSymlinks(Path path) throws IOException {
        Path absolute = path.toAbsolutePath().normalize();
        for (Path current = absolute; current != null; current = current.getParent()) {
            if (Files.isSymbolicLink(current)) {
                throw new IOException("Symbolic links are not supported");
            }
        }
    }

    public static Manifest readManifest(Path archive) throws IOException {
        rejectSymlinks(archive);

        try (ZipFile zip = new ZipFile(archive.toFile())) {
            ZipEntry entry = zip.getEntry(MANIFEST_ENTRY);
            if (entry == null || entry.isDirectory() || entry.getSize() > 16_384) {
                throw new IOException("Missing or oversized backup metadata");
            }

            try (InputStream input = zip.getInputStream(entry)) {
                byte[] data = input.readNBytes(16_385);
                if (data.length > 16_384) {
                    throw new IOException("Oversized backup metadata");
                }

                Manifest manifest;
                try {
                    manifest = GSON.fromJson(new String(data, StandardCharsets.UTF_8), Manifest.class);
                } catch (RuntimeException ex) {
                    throw new IOException("Invalid backup metadata", ex);
                }
                if (manifest == null) {
                    throw new IOException("Missing backup metadata");
                }

                manifest.validate();
                return manifest;
            }
        }
    }

    public static void write(Path source, Path archive, Manifest manifest) throws IOException {
        manifest.validate();
        rejectSymlinks(source);
        rejectSymlinks(archive);

        if (!Files.isDirectory(source, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("World directory is missing");
        }

        try (OutputStream output = Files.newOutputStream(archive); ZipOutputStream zip = new ZipOutputStream(output)) {
            zip.putNextEntry(new ZipEntry(MANIFEST_ENTRY));
            zip.write(GSON.toJson(manifest).getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
            Files.walkFileTree(source, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (attrs.isSymbolicLink()) {
                        throw new IOException("Symbolic links are not supported");
                    }

                    if (!dir.equals(source)) {
                        zip.putNextEntry(new ZipEntry(WORLD_PREFIX + entryName(source.relativize(dir)) + "/"));
                        zip.closeEntry();
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (!attrs.isRegularFile()) {
                        throw new IOException("Unsupported world file");
                    }

                    if (file.getFileName().toString().equals("session.lock")) {
                        return FileVisitResult.CONTINUE;
                    }

                    zip.putNextEntry(new ZipEntry(WORLD_PREFIX + entryName(source.relativize(file))));

                    Files.copy(file, zip);

                    zip.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    public static void extract(Path archive, Path target, Manifest expected) throws IOException {
        rejectSymlinks(target);

        Manifest actual = readManifest(archive);
        if (!actual.equals(expected)) {
            throw new IOException("Backup metadata changed");
        }

        Set<String> seen = new HashSet<>();
        boolean hasWorldFile = false;
        try (ZipInputStream zip = new ZipInputStream(Files.newInputStream(archive))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                String name = entry.getName();
                if (!seen.add(name)) {
                    throw new IOException("Duplicate archive entry");
                }

                if (name.equals(MANIFEST_ENTRY)) {
                    zip.closeEntry();
                    continue;
                }

                if (!name.startsWith(WORLD_PREFIX) || name.equals(WORLD_PREFIX)) {
                    throw new IOException("Unsafe archive entry");
                }

                String relativeName = name.substring(WORLD_PREFIX.length());
                boolean directory = entry.isDirectory();
                if (directory) {
                    relativeName = relativeName.substring(0, relativeName.length() - 1);
                }
                Path relative = safeRelative(relativeName);

                Path destination = target.resolve(relative).normalize();
                if (!destination.startsWith(target)) {
                    throw new IOException("Unsafe archive entry");
                }

                if (directory) {
                    Files.createDirectories(destination);
                } else {
                    Files.createDirectories(destination.getParent());
                    try (OutputStream output = Files.newOutputStream(destination)) {
                        zip.transferTo(output);
                    }
                    hasWorldFile = true;
                }
                zip.closeEntry();
            }
        }
        if (!hasWorldFile) {
            throw new IOException("Backup contains no world files");
        }
    }

    private static String entryName(Path path) {
        return path.toString().replace(path.getFileSystem().getSeparator(), "/");
    }

    public record Manifest(String id, long createdAt, String sourceName, String sourceId, String sourceRelativePath,
                           long seed, String environment, String generator, boolean generateStructures) {
        public void validate() throws IOException {
            try {
                UUID.fromString(sourceId);
                World.Environment.valueOf(environment);
            } catch (IllegalArgumentException | NullPointerException ex) {
                throw new IOException("Invalid backup metadata", ex);
            }

            if (!validBackupId(id) || createdAt <= 0 || !validName(sourceName) || generator == null || generator.isBlank()) {
                throw new IOException("Invalid backup metadata");
            }

            Path relative = safeRelative(sourceRelativePath);
            if (!relative.getFileName().toString().equals(sourceName)) {
                throw new IOException("Backup world path does not match its name");
            }
            if (relative.getNameCount() != 1 && (relative.getNameCount() != 4
                    || !validName(relative.getName(0).toString())
                    || !relative.getName(1).toString().equals("dimensions")
                    || !relative.getName(2).toString().equals("minecraft"))) {
                throw new IOException("Unsupported world path in backup");
            }
        }
    }
}
