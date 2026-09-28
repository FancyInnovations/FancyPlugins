package com.fancyinnovations.fancyworlds.dialogs;

import com.fancyinnovations.fancydialogs.api.data.DialogBodyData;
import com.fancyinnovations.fancydialogs.api.data.DialogButton;
import com.fancyinnovations.fancydialogs.api.data.inputs.DialogInputs;
import com.fancyinnovations.fancydialogs.api.data.inputs.DialogTextField;
import com.fancyinnovations.fancydialogs.api.dialogs.ConfirmationDialog;
import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.backups.BackupArchive;
import com.fancyinnovations.fancyworlds.backups.WorldBackupService;
import com.fancyinnovations.fancyworlds.dialogs.WorldsDialogController.Choice;
import com.fancyinnovations.fancyworlds.main.FancyWorldsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Backup browsing, creation, and restore dialogs.
 */
final class BackupDialogs {

    private static final int PAGE_SIZE = 8;

    private final FancyWorldsPlugin plugin;
    private final WorldsDialogController controller;

    BackupDialogs(FancyWorldsPlugin plugin, WorldsDialogController controller) {
        this.plugin = plugin;
        this.controller = controller;
    }

    void openBackupList(Player player, String worldId, int requestedPage, int worldPage) {
        if (!controller.allowed(player, "world.menu") || !controller.allowed(player, "world.backup.list")) {
            return;
        }

        FWorld world = plugin.getWorldService().getWorldByID(worldId);
        if (world == null) {
            controller.openWorldList(player, worldPage);
            return;
        }

        List<BackupArchive.Manifest> entries;
        try {
            entries = plugin.getBackupService().list(world.getName());
        } catch (IOException ex) {
            controller.send(player, "commands.world.backup.failed", "worldName", world.getName());
            controller.openWorldDetail(player, worldId, worldPage);
            return;
        }

        int pages = Math.max(1, (entries.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        int page = Math.clamp(requestedPage, 1, pages);

        List<DialogBodyData> body = new ArrayList<>();
        body.add(controller.line(controller.tr("backup_list.count", "count", entries.size(), "page", page, "pages", pages)));
        if (entries.isEmpty()) {
            body.add(controller.line(controller.tr("backup_list.empty")));
        }

        List<DialogButton> buttons = new ArrayList<>();
        Map<String, Choice> choices = new HashMap<>();
        for (BackupArchive.Manifest entry : entries.subList((page - 1) * PAGE_SIZE, Math.min(page * PAGE_SIZE, entries.size()))) {
            controller.add(buttons, choices, controller.tr("backup_list.entry", "createdAt", java.time.Instant.ofEpochMilli(entry.createdAt()).toString(),
                    "backupId", entry.id()), "backup_detail", entry.id(), worldPage);
        }

        if (page > 1) {
            controller.add(buttons, choices, controller.tr("common.previous"), "backup_list", worldId + ":" + (page - 1), worldPage);
        }
        if (page < pages) {
            controller.add(buttons, choices, controller.tr("common.next"), "backup_list", worldId + ":" + (page + 1), worldPage);
        }

        controller.add(buttons, choices, controller.tr("common.back"), "world", worldId, worldPage);
        controller.show(player, controller.tr("backup_list.title", "name", world.getName()), body, DialogInputs.EMPTY, buttons, choices);
    }

    void openBackupDetail(Player player, String backupId, int worldPage) {
        if (!controller.allowed(player, "world.menu") || !controller.allowed(player, "world.backup.list")) {
            return;
        }

        BackupArchive.Manifest backup;
        try {
            backup = plugin.getBackupService().find(backupId);
        } catch (IOException ex) {
            controller.send(player, "commands.world.backup.invalid_archive", "backupId", backupId);
            controller.openWorldList(player, worldPage);
            return;
        }

        FWorld world = plugin.getWorldService().getWorldByName(backup.sourceName());
        if (world == null) {
            controller.openWorldList(player, worldPage);
            return;
        }

        List<DialogBodyData> body = List.of(
                controller.line(controller.tr("backup_detail.id", "backupId", backup.id())),
                controller.line(controller.tr("backup_detail.created", "createdAt", java.time.Instant.ofEpochMilli(backup.createdAt()).toString()))
        );

        List<DialogButton> buttons = new ArrayList<>();
        Map<String, Choice> choices = new HashMap<>();
        DialogInputs inputs = DialogInputs.EMPTY;
        if (controller.allowed(player, "world.backup.restore")) {
            if (!world.isWorldLoaded() && world.getID().toString().equals(backup.sourceId())) {
                controller.add(buttons, choices, controller.tr("backup_detail.replace"), "backup_replace", backupId, worldPage);
            }

            inputs = new DialogInputs(
                    List.of(
                            new DialogTextField("newName", controller.tr("backup_detail.new_name"), 1, "", 64, 1, Map.of(), 220)
                    ),
                    List.of(),
                    List.of());

            controller.add(buttons, choices, controller.tr("backup_detail.restore_as"), "backup_restore_as", backupId, worldPage);
        }

        controller.add(buttons, choices, controller.tr("common.back"), "backup_list", world.getID() + ":1", worldPage);
        controller.show(player, controller.tr("backup_detail.title", "name", world.getName()), body, inputs, buttons, choices);
    }

    void confirmBackupReplace(Player player, Choice choice) {
        if (!controller.allowed(player, "world.backup.restore")) {
            return;
        }

        BackupArchive.Manifest backup;
        try {
            backup = plugin.getBackupService().find(choice.target());
        } catch (IOException ex) {
            controller.send(player, "commands.world.backup.invalid_archive", "backupId", choice.target());
            return;
        }

        FWorld world = plugin.getWorldService().getWorldByName(backup.sourceName());
        if (world == null || world.isWorldLoaded() || !world.getID().toString().equals(backup.sourceId())) {
            controller.send(player, "dialogs.common.action_unavailable");
            return;
        }

        controller.discard(player);

        new ConfirmationDialog(controller.tr("backup_detail.replace_question", "name", backup.sourceName(), "backupId", backup.id()))
                .withTitle(controller.tr("backup_detail.replace"))
                .withOnConfirm(() -> Bukkit.getScheduler().runTask(plugin, () -> {
                    if (!player.isOnline() || !controller.allowed(player, "world.backup.restore")) {
                        return;
                    }

                    controller.send(player, "commands.world.backup.progress", "worldName", backup.sourceName());

                    plugin.getBackupService().restore(backup.id(), null).thenAccept(outcome -> {
                        if (!player.isOnline()) return;
                        sendBackupResult(player, outcome);
                        openBackupDetail(player, backup.id(), choice.page());
                    });
                }))
                .withOnCancel(() -> Bukkit.getScheduler().runTask(plugin, () -> openBackupDetail(player, backup.id(), choice.page())))
                .ask(player);
    }

    void restoreAsBackup(Player player, Choice choice, Map<String, String> inputs) {
        if (!controller.allowed(player, "world.backup.restore")) {
            return;
        }

        String name = inputs.getOrDefault("newName", "").trim();
        if (!BackupArchive.validName(name)) {
            controller.send(player, "commands.world.backup.invalid_name", "worldName", name);
            openBackupDetail(player, choice.target(), choice.page());
            return;
        }

        controller.send(player, "commands.world.backup.progress", "worldName", name);

        plugin.getBackupService().restore(choice.target(), name).thenAccept(outcome -> {
            if (!player.isOnline()) return;
            sendBackupResult(player, outcome);
            openBackupDetail(player, choice.target(), choice.page());
        });
    }

    private void sendBackupResult(Player player, WorldBackupService.Outcome outcome) {
        String key = switch (outcome.status()) {
            case CREATED -> "created";
            case RESTORED -> "restored";
            case CLONED -> "cloned";
            case LOADED -> "loaded";
            case BUSY -> "busy";
            case NOT_FOUND -> "not_found";
            case INVALID_NAME -> "invalid_name";
            case NAME_EXISTS -> "name_exists";
            case INVALID_ARCHIVE -> "invalid_archive";
            case FAILED -> "failed";
        };

        controller.send(player, "commands.world.backup." + key, "worldName", outcome.worldName(), "backupId", outcome.backupId());
    }

    void createBackup(Player player, FWorld world, Choice choice) {
        controller.send(player, "commands.world.backup.progress", "worldName", world.getName());

        plugin.getBackupService().create(world).thenAccept(outcome -> {
            if (!player.isOnline()) return;
            sendBackupResult(player, outcome);
            controller.openWorldDetail(player, choice.target(), choice.page());
        });
    }
}
