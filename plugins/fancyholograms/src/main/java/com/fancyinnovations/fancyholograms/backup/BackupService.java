package com.fancyinnovations.fancyholograms.backup;

import com.fancyinnovations.fancyholograms.api.HologramRegistry;
import com.fancyinnovations.fancyholograms.api.data.HologramData;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancyanalytics.logger.properties.StringProperty;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class BackupService {

    public static final File BACKUP_FOLDER = new File("plugins/FancyHolograms/backups");
    private static final File DATA_FOLDER = new File("plugins/FancyHolograms/data");

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    public BackupService() {
        // Schedule the backup task to run every hour
        FancyHologramsPlugin.get().getHologramThread().scheduleWithFixedDelay(
                this::runBackupTask,
                20L,
                60, TimeUnit.MINUTES
        );
    }

    private static void zip(Path sourceDir, Path zipPath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            Files.walk(sourceDir)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourceDir.relativize(path).toString());
                        try {
                            zos.putNextEntry(zipEntry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            FancyHologramsPlugin.get().getFancyLogger().error(
                                    "Failed to zip file",
                                    StringProperty.of("file", path.toString()),
                                    ThrowableProperty.of(e)
                            );
                        }
                    });
        }
    }

    private static void unzip(Path zipPath, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipPath))) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                Path newPath = targetDir.resolve(zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    Files.createDirectories(newPath);
                } else {
                    Files.createDirectories(newPath.getParent());
                    Files.copy(zis, newPath);
                }
                zis.closeEntry();
            }
        }
    }

    /**
     * Creates a backup of the data folder by zipping its contents into a timestamped zip file in the backup folder.
     * If the backup folder does not exist, it will be created.
     *
     * @return true if the backup was created successfully, false otherwise
     */
    public boolean createBackup() {
        if (!BACKUP_FOLDER.exists()) {
            BACKUP_FOLDER.mkdirs();
        }

        String date = DATE_FORMAT.format(new Date(System.currentTimeMillis()));

        File backupFile = new File(BACKUP_FOLDER, "FH_" + date + ".zip");
        try {
            zip(DATA_FOLDER.toPath(), backupFile.toPath());
        } catch (IOException e) {
            FancyHologramsPlugin.get().getFancyLogger().error(
                    "Failed to create backup",
                    StringProperty.of("file", backupFile.toString()),
                    ThrowableProperty.of(e)
            );
            return false;
        }

        return true;
    }

    /**
     * Lists all backup files in the backup folder.
     *
     * @return a list of backup files
     */
    public List<File> listBackups() {
        if (!BACKUP_FOLDER.exists()) {
            BACKUP_FOLDER.mkdirs();
        }
        return List.of(BACKUP_FOLDER.listFiles((dir, name) -> name.endsWith(".zip")));
    }

    /**
     * Restores a backup by unzipping the specified backup file into the data folder.
     *
     * @param backupFile the backup file to restore
     * @return true if the backup was restored successfully, false otherwise
     */
    public boolean restoreBackup(File backupFile) {
        if (!backupFile.exists() || !backupFile.getName().endsWith(".zip")) {
            return false;
        }

        // Delete all holograms
        HologramRegistry hologramRegistry = FancyHologramsPlugin.get().getRegistry();
        for (Hologram hologram : hologramRegistry.getAll()) {
            hologramRegistry.unregister(hologram);
        }

        // Make sure all hologram data is gone
        try {
            Files.walk(DATA_FOLDER.toPath())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            FancyHologramsPlugin.get().getFancyLogger().error(
                    "Failed to clear data folder before restoring backup",
                    StringProperty.of("file", DATA_FOLDER.toString()),
                    ThrowableProperty.of(e)
            );
            return false;
        }

        // Unzip the backup file into the data folder
        try {
            unzip(backupFile.toPath(), DATA_FOLDER.toPath());
        } catch (IOException e) {
            FancyHologramsPlugin.get().getFancyLogger().error(
                    "Failed to restore backup",
                    StringProperty.of("file", backupFile.toString()),
                    ThrowableProperty.of(e)
            );
            return false;
        }

        // Load all holograms from the storage and register them

        Collection<HologramData> newHolograms = FancyHologramsPlugin.get().getStorage().loadAll();
        hologramRegistry.clear(); // clear again to make sure no old holograms are left
        for (HologramData data : newHolograms) {
            Hologram hologram = FancyHologramsPlugin.get().getHologramFactory().apply(data);
            hologramRegistry.register(hologram);
        }

        return true;
    }

    /**
     * Gets the timestamp of the last backup file in the backup folder.
     *
     * @return the timestamp of the last backup file, or -1 if no backup files exist
     */
    private long getLastBackupTimestamp() {
        if (!BACKUP_FOLDER.exists()) {
            BACKUP_FOLDER.mkdirs();
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
        long backupInterval = FancyHologramsPlugin.get().getConfig().getLong("backups.interval") * 60 * 60 * 1000; // convert hours to milliseconds
        if (System.currentTimeMillis() - lastBackupTimestamp >= backupInterval) {
            if (createBackup()) {
                FancyHologramsPlugin.get().getLogger().info("Created a new backup.");
            } else {
                FancyHologramsPlugin.get().getLogger().warning("Failed to create a new backup.");
            }
        }

        // Check to delete old backups
        long backupRetention = FancyHologramsPlugin.get().getConfig().getLong("backups.retention") * 24 * 60 * 60 * 1000; // convert days to milliseconds
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
}