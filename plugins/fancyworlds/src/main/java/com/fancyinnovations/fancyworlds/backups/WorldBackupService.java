package com.fancyinnovations.fancyworlds.backups;

import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.main.FancyWorldsPlugin;
import com.fancyinnovations.fancyworlds.utils.WorldFileUtils;
import com.fancyinnovations.fancyworlds.worlds.FWorldImpl;
import com.fancyinnovations.fancyworlds.worlds.FWorldSettingsImpl;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Coordinates unloaded-world backups. Public entry points are called on the server thread.
 */
public final class WorldBackupService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    private final FancyWorldsPlugin plugin;
    private final Path worldContainer;
    private final Path backupDirectory;
    private final Set<String> busyWorlds = ConcurrentHashMap.newKeySet();

    public WorldBackupService(FancyWorldsPlugin plugin) {
        this.plugin = plugin;
        this.worldContainer = Bukkit.getWorldContainer().toPath().toAbsolutePath().normalize();
        this.backupDirectory = plugin.getDataFolder().toPath().toAbsolutePath().normalize().resolve("backups");
    }

    private static void deleteTree(Path path) throws IOException {
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            return;
        }

        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException error) throws IOException {
                if (error != null) throw error;
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static CompletableFuture<Outcome> done(Status status, String backupId, String worldName) {
        return CompletableFuture.completedFuture(new Outcome(status, backupId, worldName));
    }

    public boolean isBusy(String name) {
        return busyWorlds.contains(name);
    }

    public List<BackupArchive.Manifest> list(String worldName) throws IOException {
        if (!BackupArchive.validName(worldName)) return List.of();
        return list().stream().filter(manifest -> manifest.sourceName().equals(worldName)).toList();
    }

    public List<BackupArchive.Manifest> list() throws IOException {
        BackupArchive.rejectSymlinks(backupDirectory);

        if (!Files.exists(backupDirectory, LinkOption.NOFOLLOW_LINKS)) {
            return List.of();
        }

        List<BackupArchive.Manifest> backups = new ArrayList<>();
        try (DirectoryStream<Path> files = Files.newDirectoryStream(backupDirectory, "*.zip")) {
            for (Path file : files) {
                try {
                    BackupArchive.Manifest manifest = BackupArchive.readManifest(file);
                    if (file.getFileName().toString().equals(manifest.id() + ".zip")) {
                        backups.add(manifest);
                    }
                } catch (IOException ignored) {
                    // A damaged backup does not hide healthy backups from the list.
                }
            }
        }

        backups.sort(Comparator.comparingLong(BackupArchive.Manifest::createdAt).reversed());

        return backups;
    }

    public BackupArchive.Manifest find(String backupId) throws IOException {
        if (!BackupArchive.validBackupId(backupId)) {
            throw new IOException("Invalid backup ID");
        }

        Path archive = backupDirectory.resolve(backupId + ".zip");

        BackupArchive.rejectSymlinks(archive);

        if (!Files.isRegularFile(archive, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("Backup not found");
        }

        BackupArchive.Manifest manifest = BackupArchive.readManifest(archive);

        if (!manifest.id().equals(backupId)) {
            throw new IOException("Backup ID mismatch");
        }
        return manifest;
    }

    public CompletableFuture<Outcome> create(FWorld world) {
        String name = world.getName();
        if (world.isWorldLoaded()) {
            return done(Status.LOADED, null, name);
        }
        if (!busyWorlds.add(name)) {
            return done(Status.BUSY, null, name);
        }

        Path source;
        BackupArchive.Manifest manifest;
        try {
            source = checkedWorldPath(WorldFileUtils.getWorldDirectory(name));
            if (!Files.isDirectory(source, LinkOption.NOFOLLOW_LINKS)) {
                throw new IOException("World directory is missing");
            }

            if (backupDirectory.startsWith(source)) {
                throw new IOException("Backup directory is inside the world");
            }

            String relative = worldContainer.relativize(source).toString().replace('\\', '/');
            BackupArchive.rejectSymlinks(backupDirectory);

            Files.createDirectories(backupDirectory);

            long createdAt = System.currentTimeMillis();
            String baseId = "FW_" + name + "_" + DATE_FORMAT.format(Instant.ofEpochMilli(createdAt).atZone(ZoneId.systemDefault()));
            String backupId = baseId;
            for (int suffix = 1; Files.exists(backupDirectory.resolve(backupId + ".zip"), LinkOption.NOFOLLOW_LINKS); suffix++) {
                backupId = baseId + "_" + suffix;
            }

            manifest = new BackupArchive.Manifest(
                    backupId,
                    createdAt,
                    name,
                    world.getID().toString(),
                    relative,
                    world.getSeed(),
                    world.getEnvironment().name(),
                    world.getGenerator(),
                    world.canGenerateStructures()
            );

            manifest.validate();
        } catch (IOException ex) {
            plugin.getLogger().warning("World backup failed: " + ex.getMessage());
            busyWorlds.remove(name);
            return done(Status.FAILED, null, name);
        }

        CompletableFuture<Outcome> future = new CompletableFuture<>();

        Path temporary = backupDirectory.resolve("." + manifest.id() + ".tmp");
        Path archive = backupDirectory.resolve(manifest.id() + ".zip");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            IOException failure = null;
            try {
                BackupArchive.write(source, temporary, manifest);
            } catch (IOException ex) {
                failure = ex;
            }

            IOException finalFailure = failure;
            Bukkit.getScheduler().runTask(plugin, () -> {
                Status status = Status.FAILED;
                try {
                    if (finalFailure != null) {
                        throw finalFailure;
                    }

                    if (world.isWorldLoaded()) {
                        status = Status.LOADED;
                    } else if (plugin.getWorldService().getWorldByID(world.getID().toString()) != world) {
                        status = Status.NOT_FOUND;
                    } else {
                        checkedWorldPath(source);
                        if (!Files.isDirectory(source, LinkOption.NOFOLLOW_LINKS)) {
                            throw new IOException("World directory is missing");
                        }

                        WorldBackupFiles.move(temporary, archive);
                        status = Status.CREATED;
                    }
                } catch (IOException ex) {
                    plugin.getLogger().warning("World backup failed: " + ex.getMessage());
                } finally {
                    busyWorlds.remove(name);
                    if (status != Status.CREATED) {
                        cleanupAsync(temporary);
                    }
                    future.complete(new Outcome(status, manifest.id(), name));
                }
            });
        });
        return future;
    }

    public CompletableFuture<Outcome> restore(String backupId, String newName) {
        BackupArchive.Manifest manifest;
        try {
            manifest = find(backupId);
        } catch (IOException ex) {
            return done(Status.INVALID_ARCHIVE, backupId, newName);
        }

        boolean clone = newName != null;
        String destinationName = clone ? newName : manifest.sourceName();
        if (!BackupArchive.validName(destinationName)) {
            return done(Status.INVALID_NAME, backupId, destinationName);
        }

        FWorld original = plugin.getWorldService().getWorldByName(manifest.sourceName());
        if (!clone && (original == null || !original.getID().toString().equals(manifest.sourceId()))) {
            return done(Status.NOT_FOUND, backupId, destinationName);
        }
        if (!clone && original.isWorldLoaded()) {
            return done(Status.LOADED, backupId, destinationName);
        }
        if (clone && (plugin.getWorldService().getWorldByName(destinationName) != null || WorldFileUtils.isWorldOnDisk(destinationName))) {
            return done(Status.NAME_EXISTS, backupId, destinationName);
        }
        if (!busyWorlds.add(destinationName)) {
            return done(Status.BUSY, backupId, destinationName);
        }

        Path destination;
        try {
            Path relative = BackupArchive.safeRelative(manifest.sourceRelativePath());
            if (clone) {
                Path parent = relative.getParent();
                relative = parent == null || !Files.isDirectory(worldContainer.resolve(parent), LinkOption.NOFOLLOW_LINKS)
                        ? Path.of(destinationName) : parent.resolve(destinationName);
            }

            destination = checkedWorldPath(worldContainer.resolve(relative));
            if (clone && Files.exists(destination, LinkOption.NOFOLLOW_LINKS)) {
                throw new IOException("World already exists");
            }
            if (!clone && !Files.isDirectory(destination, LinkOption.NOFOLLOW_LINKS)) {
                throw new IOException("World directory is missing");
            }
            if (!clone && !checkedWorldPath(WorldFileUtils.getWorldDirectory(destinationName)).equals(destination)) {
                throw new IOException("World directory has moved since backup creation");
            }

            BackupArchive.rejectSymlinks(destination.getParent());

        } catch (IOException ex) {
            plugin.getLogger().warning("World restore failed: " + ex.getMessage());
            busyWorlds.remove(destinationName);
            return done(Status.FAILED, backupId, destinationName);
        }

        Path archive = backupDirectory.resolve(manifest.id() + ".zip");
        CompletableFuture<Outcome> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Path staging = null;
            IOException failure = null;
            try {
                staging = Files.createTempDirectory(destination.getParent(), ".fw-restore-");
                BackupArchive.extract(archive, staging, manifest);
                if (clone) WorldBackupFiles.prepareClone(staging);
            } catch (IOException ex) {
                failure = ex;
            }

            Path prepared = staging;
            IOException finalFailure = failure;
            Bukkit.getScheduler().runTask(plugin, () -> {
                Status status = Status.FAILED;
                Path oldDirectory = null;
                try {
                    if (finalFailure != null) {
                        throw finalFailure;
                    }
                    if (prepared == null) {
                        throw new IOException("Restore staging is missing");
                    }

                    checkedWorldPath(destination);

                    if (clone) {
                        if (plugin.getWorldService().getWorldByName(destinationName) != null || WorldFileUtils.isWorldOnDisk(destinationName)
                                || Files.exists(destination, LinkOption.NOFOLLOW_LINKS)) {
                            status = Status.NAME_EXISTS;
                        } else {
                            WorldBackupFiles.move(prepared, destination);
                            try {
                                FWorldImpl restored = new FWorldImpl(
                                        UUID.randomUUID(),
                                        destinationName, manifest.seed(),
                                        World.Environment.valueOf(manifest.environment()),
                                        manifest.generator(),
                                        manifest.generateStructures(),
                                        new FWorldSettingsImpl()
                                );
                                plugin.getWorldService().registerWorld(restored);
                                status = Status.CLONED;
                            } catch (RuntimeException ex) {
                                WorldBackupFiles.move(destination, prepared);
                                throw ex;
                            }
                        }
                    } else if (original.isWorldLoaded()) {
                        status = Status.LOADED;
                    } else if (plugin.getWorldService().getWorldByID(original.getID().toString()) != original
                            || !Files.isDirectory(destination, LinkOption.NOFOLLOW_LINKS)
                            || !checkedWorldPath(WorldFileUtils.getWorldDirectory(destinationName)).equals(destination)) {
                        status = Status.NOT_FOUND;
                    } else {
                        oldDirectory = WorldBackupFiles.replace(prepared, destination);
                        status = Status.RESTORED;
                    }
                } catch (IOException | RuntimeException ex) {
                    plugin.getLogger().warning("World restore failed: " + ex.getMessage());
                } finally {
                    busyWorlds.remove(destinationName);
                    if (prepared != null && status != Status.RESTORED && status != Status.CLONED) {
                        cleanupAsync(prepared);
                    }
                    if (oldDirectory != null && status == Status.RESTORED) {
                        cleanupAsync(oldDirectory);
                    }
                    future.complete(new Outcome(status, manifest.id(), destinationName));
                }
            });
        });
        return future;
    }

    private Path checkedWorldPath(Path path) throws IOException {
        Path result = path.toAbsolutePath().normalize();
        if (result.equals(worldContainer) || !result.startsWith(worldContainer)) {
            throw new IOException("Unsafe world path");
        }

        BackupArchive.rejectSymlinks(result);
        return result;
    }

    private void cleanupAsync(Path path) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                deleteTree(path);
            } catch (IOException ex) {
                plugin.getLogger().warning("Could not remove backup temporary files: " + path);
            }
        });
    }

    public enum Status {
        CREATED,
        RESTORED,
        CLONED,
        LOADED,
        BUSY,
        NOT_FOUND,
        INVALID_NAME,
        NAME_EXISTS,
        INVALID_ARCHIVE,
        FAILED,
    }

    public record Outcome(Status status, String backupId, String worldName) {
    }
}
