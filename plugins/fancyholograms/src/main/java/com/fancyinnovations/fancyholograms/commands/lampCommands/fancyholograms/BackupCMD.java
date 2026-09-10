package com.fancyinnovations.fancyholograms.commands.lampCommands.fancyholograms;

import com.fancyinnovations.fancyholograms.backup.BackupService;
import com.fancyinnovations.fancyholograms.commands.lampCommands.suggestions.BackupFilesSuggestion;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.translations.Translator;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.io.File;
import java.util.List;

public class BackupCMD {

    public static final BackupCMD INSTANCE = new BackupCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private BackupCMD() {
    }

    @Command("fancyholograms backup create")
    @Description("Creates a backup of all holograms and their data")
    @CommandPermission("fancyholograms.commands.fancyholograms.backup.create")
    public void create(
            final BukkitCommandActor actor
    ) {
        boolean success = plugin.getBackupService().createBackup();

        if (success) {
            translator.translate("commands.fancyholograms.backup.create.success")
                    .withPrefix()
                    .send(actor.sender());
        } else {
            translator.translate("commands.fancyholograms.backup.create.failed")
                    .withPrefix()
                    .send(actor.sender());
        }
    }

    @Command("fancyholograms backup list")
    @Description("Lists all backups that are currently available in the backups folder")
    @CommandPermission("fancyholograms.commands.fancyholograms.backup.list")
    public void list(
            final BukkitCommandActor actor
    ) {
        List<File> backupFiles = plugin.getBackupService().listBackups();

        if (backupFiles.isEmpty()) {
            translator.translate("commands.fancyholograms.backup.list.empty")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        translator.translate("commands.fancyholograms.backup.list.header")
                .withPrefix()
                .replace("count", String.valueOf(backupFiles.size()))
                .send(actor.sender());

        for (File backupFile : backupFiles) {
            translator.translate("commands.fancyholograms.backup.list.entry")
                    .withPrefix()
                    .replace("file", backupFile.getName())
                    .send(actor.sender());
        }
    }

    @Command("fancyholograms backup restore <file>")
    @Description("Restores a backup from the specified file")
    @CommandPermission("fancyholograms.commands.fancyholograms.backup.restore")
    public void restore(
            final BukkitCommandActor actor,
            final @SuggestWith(BackupFilesSuggestion.class) String file
    ) {
        File backupFile = new File(BackupService.BACKUP_FOLDER, file);

        if (!backupFile.exists()) {
            translator.translate("commands.fancyholograms.backup.restore.not_found")
                    .withPrefix()
                    .replace("file", file)
                    .send(actor.sender());
            return;
        }

        boolean success = plugin.getBackupService().restoreBackup(backupFile);

        if (success) {
            translator.translate("commands.fancyholograms.backup.restore.success")
                    .withPrefix()
                    .replace("file", file)
                    .send(actor.sender());
        } else {
            translator.translate("commands.fancyholograms.backup.restore.failed")
                    .withPrefix()
                    .replace("file", file)
                    .send(actor.sender());
        }
    }

    @Command("fancyholograms backup delete <file>")
    @Description("Deletes a backup file")
    @CommandPermission("fancyholograms.commands.fancyholograms.backup.delete")
    public void delete(
            final BukkitCommandActor actor,
            final @SuggestWith(BackupFilesSuggestion.class) String file
    ) {
        File backupFile = new File(BackupService.BACKUP_FOLDER, file);

        if (!backupFile.exists()) {
            translator.translate("commands.fancyholograms.backup.delete.not_found")
                    .withPrefix()
                    .replace("file", file)
                    .send(actor.sender());
            return;
        }

        boolean success = backupFile.delete();

        if (success) {
            translator.translate("commands.fancyholograms.backup.delete.success")
                    .withPrefix()
                    .replace("file", file)
                    .send(actor.sender());
        } else {
            translator.translate("commands.fancyholograms.backup.delete.failed")
                    .withPrefix()
                    .replace("file", file)
                    .send(actor.sender());
        }
    }

}
