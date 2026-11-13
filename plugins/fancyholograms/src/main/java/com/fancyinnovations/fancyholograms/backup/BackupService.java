package com.fancyinnovations.fancyholograms.backup;

import com.fancyinnovations.fancyholograms.api.HologramRegistry;
import com.fancyinnovations.fancyholograms.api.data.HologramData;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import com.fancyinnovations.fancyholograms.storage.json.JsonStorage;
import com.google.gson.JsonParser;
import de.oliver.fancyanalytics.logger.properties.StringProperty;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class BackupService {

    public static final File BACKUP_FOLDER = new File("plugins/FancyHolograms/backups");
    private static final File DATA_FOLDER = new File("plugins/FancyHolograms/data");

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    private final File dataFolder;
    private final File backupFolder;

    public BackupService() {
        this(DATA_FOLDER, BACKUP_FOLDER);

        // Schedule the backup task to run every hour
        FancyHologramsPlugin.get().getHologramThread().scheduleWithFixedDelay(
                this::runBackupTask,
                20L,
                60, TimeUnit.MINUTES
        );
    }

    BackupService(File dataFolder, File backupFolder) {
        this.dataFolder = dataFolder;
        this.backupFolder = backupFolder;
    }

    private static void zip(Path sourceDir, Path zipPath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath));
             Stream<Path> paths = Files.walk(sourceDir)) {
            for (Path path : paths.filter(Files::isRegularFile).toList()) {
                ZipEntry zipEntry = new ZipEntry(sourceDir.relativize(path).toString().replace(File.separatorChar, '/'));
                zos.putNextEntry(zipEntry);
                Files.copy(path, zos);
                zos.closeEntry();
            }
        }
    }

    private static void unzip(Path zipPath, Path targetDir) throws IOException {
        boolean hasData = false;
        try (ZipFile zip = new ZipFile(zipPath.toFile())) {
            var entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                Path newPath = targetDir.resolve(zipEntry.getName()).normalize();
                if (!newPath.startsWith(targetDir) || zipEntry.getName().contains("\\")) {
                    throw new IOException("Invalid backup entry: " + zipEntry.getName());
                }
                if (zipEntry.isDirectory()) {
                    Files.createDirectories(newPath);
                } else {
                    Files.createDirectories(newPath.getParent());
                    CRC32 checksum = new CRC32();
                    try (CheckedInputStream input = new CheckedInputStream(zip.getInputStream(zipEntry), checksum)) {
                        Files.copy(input, newPath);
                    }
                    if (checksum.getValue() != zipEntry.getCrc() || Files.size(newPath) != zipEntry.getSize()) {
                        throw new IOException("Corrupt backup entry: " + zipEntry.getName());
                    }
                    if (newPath.getFileName().toString().endsWith(".json")) {
                        try (BufferedReader reader = Files.newBufferedReader(newPath)) {
                            JsonParser.parseReader(reader);
                        }
                        Path relative = targetDir.relativize(newPath);
                        if (relative.getNameCount() > 1 && (relative.getName(0).toString().equals("holograms") || relative.getName(0).toString().equals("traits"))) {
                            hasData = true;
                        }
                    }
                }
            }
        }
        if (!hasData) {
            throw new IOException("Backup contains no hologram or trait data");
        }
    }

    /**
     * Creates a backup of the data folder by zipping its contents into a timestamped zip file in the backup folder.
     * If the backup folder does not exist, it will be created.
     *
     * @return the result of the backup operation
     */
    public BackupResult createBackup() {
        synchronized (FancyHologramsPlugin.get().getStorage()) {
            return createBackupLocked();
        }
    }

    private BackupResult createBackupLocked() {
        if (!dataFolder.isDirectory()) {
            return BackupResult.NO_DATA;
        }

        try (Stream<Path> paths = Files.walk(dataFolder.toPath())) {
            if (paths.noneMatch(Files::isRegularFile)) {
                return BackupResult.NO_DATA;
            }
        } catch (IOException | UncheckedIOException e) {
            FancyHologramsPlugin.get().getFancyLogger().error(
                    "Failed to inspect data folder before creating backup",
                    StringProperty.of("file", dataFolder.toString()),
                    ThrowableProperty.of(e)
            );
            return BackupResult.FAILED;
        }

        if (!backupFolder.exists()) {
            backupFolder.mkdirs();
        }

        String date = DATE_FORMAT.format(new Date(System.currentTimeMillis()));

        File backupFile = new File(backupFolder, "FH_" + date + ".zip");
        Path temporary = null;
        try {
            temporary = Files.createTempFile(backupFolder.toPath(), ".FH_", ".zip.tmp");
            zip(dataFolder.toPath(), temporary);
            Files.move(temporary, backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | UncheckedIOException e) {
            FancyHologramsPlugin.get().getFancyLogger().error(
                    "Failed to create backup",
                    StringProperty.of("file", backupFile.toString()),
                    ThrowableProperty.of(e)
            );
            return BackupResult.FAILED;
        } finally {
            if (temporary != null) {
                try {
                    Files.deleteIfExists(temporary);
                } catch (IOException e) {
                    FancyHologramsPlugin.get().getFancyLogger().warn("Failed to remove temporary backup", ThrowableProperty.of(e));
                }
            }
        }

        return BackupResult.CREATED;
    }

    /**
     * Lists all backup files in the backup folder.
     *
     * @return a list of backup files
     */
    public List<File> listBackups() {
        if (!backupFolder.exists()) {
            backupFolder.mkdirs();
        }
        return List.of(backupFolder.listFiles((dir, name) -> name.endsWith(".zip")));
    }

    /**
     * Restores a backup by unzipping the specified backup file into the data folder.
     *
     * @param backupFile the backup file to restore
     * @return true if the backup was restored successfully, false otherwise
     */
    public boolean restoreBackup(File backupFile) {
        if (!backupFile.isFile() || !backupFile.getName().endsWith(".zip")) {
            return false;
        }

        synchronized (FancyHologramsPlugin.get().getStorage()) {
            return restoreBackupLocked(backupFile);
        }
    }

    private boolean restoreBackupLocked(File backupFile) {
        HologramRegistry registry = FancyHologramsPlugin.get().getRegistry();
        Collection<Hologram> previousHolograms = new ArrayList<>(registry.getAll());
        Path dataPath = dataFolder.toPath().toAbsolutePath();
        Path temporary = null;
        Path previous = null;
        boolean unloaded = false;
        boolean moved = false;
        boolean restored = false;
        try {
            Files.createDirectories(dataPath.getParent());
            temporary = Files.createTempDirectory(dataPath.getParent(), ".fh-restore-");
            Path staged = temporary.resolve("data");
            previous = temporary.resolve("previous");
            Files.createDirectory(staged);
            unzip(backupFile.toPath(), staged);

            Collection<HologramData> data = new JsonStorage(staged.resolve("holograms").toFile()).loadAllStrict();
            Set<String> names = new HashSet<>();
            for (HologramData hologram : data) {
                if (!names.add(hologram.getName())) {
                    throw new IOException("Duplicate hologram name in backup: " + hologram.getName());
                }
            }

            unloaded = true;
            unloadHolograms(registry);
            if (Files.exists(dataPath)) {
                Files.move(dataPath, previous);
                moved = true;
            }
            Files.move(staged, dataPath);

            for (HologramData hologramData : data) {
                Hologram hologram = FancyHologramsPlugin.get().getHologramFactory().apply(hologramData);
                if (!registry.register(hologram)) {
                    throw new IllegalStateException("Could not register restored hologram " + hologramData.getName());
                }
            }

            restored = true;
            return true;
        } catch (IOException | RuntimeException e) {
            FancyHologramsPlugin.get().getFancyLogger().error(
                    "Failed to restore backup",
                    StringProperty.of("file", backupFile.toString()),
                    ThrowableProperty.of(e)
            );
            if (unloaded) {
                try {
                    unloadHolograms(registry);
                    if (moved || (temporary != null && !Files.exists(temporary.resolve("data")))) {
                        deleteDirectory(dataPath);
                    }
                    if (moved) {
                        Files.move(previous, dataPath);
                    }
                    for (Hologram hologram : previousHolograms) {
                        registry.register(hologram);
                        hologram.getData().setHasChanges(true);
                    }
                } catch (IOException | RuntimeException rollbackError) {
                    FancyHologramsPlugin.get().getFancyLogger().error("Failed to restore previous hologram data", ThrowableProperty.of(rollbackError));
                }
            }
            return false;
        } finally {
            if (temporary != null && (restored || previous == null || !Files.exists(previous))) {
                try {
                    deleteDirectory(temporary);
                } catch (IOException e) {
                    FancyHologramsPlugin.get().getFancyLogger().warn("Failed to remove restore staging directory", ThrowableProperty.of(e));
                }
            }
        }
    }

    private void unloadHolograms(HologramRegistry registry) {
        for (Hologram hologram : registry.getAll()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                hologram.despawnFrom(player);
            }
        }
        registry.clear();
    }

    private void deleteDirectory(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(directory)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }

    /**
     * Gets the timestamp of the last backup file in the backup folder.
     *
     * @return the timestamp of the last backup file, or -1 if no backup files exist
     */
    private long getLastBackupTimestamp() {
        if (!backupFolder.exists()) {
            backupFolder.mkdirs();
        }

        return listBackups().stream()
                .mapToLong(File::lastModified)
                .max()
                .orElse(-1);
    }

    /**
     * Runs the backup task.
     */
    private void runBackupTask() {
        // Check to create a new backup
        long lastBackupTimestamp = getLastBackupTimestamp();
        long backupInterval = FancyHologramsPlugin.get().getConfig().getLong("settings.backups.interval") * 60 * 60 * 1000; // convert hours to milliseconds
        if (System.currentTimeMillis() - lastBackupTimestamp >= backupInterval) {
            BackupResult result = createBackup();
            if (result == BackupResult.CREATED) {
                FancyHologramsPlugin.get().getLogger().info("Created a new backup.");
            } else if (result == BackupResult.FAILED) {
                FancyHologramsPlugin.get().getLogger().warning("Failed to create a new backup.");
            }
        }

        // Check to delete old backups
        long backupRetention = FancyHologramsPlugin.get().getConfig().getLong("settings.backups.retention") * 24 * 60 * 60 * 1000; // convert days to milliseconds
        for (File backupFile : listBackups()) {
            if (System.currentTimeMillis() - backupFile.lastModified() >= backupRetention) {
                if (backupFile.delete()) {
                    FancyHologramsPlugin.get().getLogger().info("Deleted old backup: " + backupFile.getName());
                } else {
                    FancyHologramsPlugin.get().getLogger().warning("Failed to delete old backup: " + backupFile.getName());
                }
            }
        }
    }

    public enum BackupResult {
        CREATED,
        NO_DATA,
        FAILED
    }
}
