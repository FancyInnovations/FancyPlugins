package com.fancyinnovations.fancyworlds.commands.world;

import com.fancyinnovations.fancydialogs.api.dialogs.ConfirmationDialog;
import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.backups.BackupArchive;
import com.fancyinnovations.fancyworlds.backups.WorldBackupService;
import com.fancyinnovations.fancyworlds.main.FancyWorldsPlugin;
import de.oliver.fancylib.translations.message.SimpleMessage;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.*;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.node.ExecutionContext;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

public final class WorldBackupCMD {

    private static final int PAGE_SIZE = 8;
    private final WorldBackupService backups;

    public WorldBackupCMD(WorldBackupService backups) {
        this.backups = backups;
    }

    @Command("world backup create")
    @Description("Creates a backup of an unloaded world")
    @CommandPermission("fancyworlds.commands.world.backup.create")
    public void create(BukkitCommandActor actor, FWorld world) {
        send(actor, "progress", world.getName(), "");
        backups.create(world).thenAccept(outcome -> sendResult(actor, outcome));
    }

    @Command("world backup list")
    @Description("Lists backups of a world")
    @CommandPermission("fancyworlds.commands.world.backup.list")
    public void list(BukkitCommandActor actor, String worldName, @Range(min = 1) @Default("1") int page) {
        List<BackupArchive.Manifest> entries;
        try {
            entries = backups.list(worldName);
        } catch (IOException ex) {
            send(actor, "failed", worldName, "");
            return;
        }

        if (entries.isEmpty()) {
            send(actor, "empty", worldName, "");
            return;
        }

        int pages = (entries.size() + PAGE_SIZE - 1) / PAGE_SIZE;
        int current = Math.min(page, pages);
        FancyWorldsPlugin.get().getTranslator().translate("commands.world.backup.list_header")
                .withPrefix().replace("worldName", worldName).replace("page", String.valueOf(current))
                .replace("pages", String.valueOf(pages)).send(actor.sender());

        for (BackupArchive.Manifest entry : entries.subList((current - 1) * PAGE_SIZE, Math.min(current * PAGE_SIZE, entries.size()))) {
            FancyWorldsPlugin.get().getTranslator().translate("commands.world.backup.list_entry")
                    .replace("backupId", entry.id()).replace("createdAt", Instant.ofEpochMilli(entry.createdAt()).toString())
                    .send(actor.sender());
        }
    }

    @Command("world backup restore")
    @Description("Replaces an unloaded world with a backup")
    @CommandPermission("fancyworlds.commands.world.backup.restore")
    public void restore(BukkitCommandActor actor, @SuggestWith(BackupNamesSuggestionProvider.class) String backupId) {
        BackupArchive.Manifest manifest;
        try {
            manifest = backups.find(backupId);
        } catch (IOException ex) {
            send(actor, "invalid_archive", "", backupId);
            return;
        }

        Runnable action = () -> {
            send(actor, "progress", manifest.sourceName(), backupId);
            backups.restore(backupId, null).thenAccept(outcome -> sendResult(actor, outcome));
        };

        if (actor.isPlayer()) {
            SimpleMessage question = (SimpleMessage) FancyWorldsPlugin.get().getTranslator()
                    .translate("commands.world.backup.confirm_replace")
                    .replace("worldName", manifest.sourceName()).replace("backupId", backupId);

            new ConfirmationDialog(question.getMessage())
                    .withTitle("Confirm restore")
                    .withOnConfirm(() -> Bukkit.getScheduler().runTask(FancyWorldsPlugin.get(), action))
                    .ask(actor.asPlayer());
        } else {
            action.run();
        }
    }

    @Command("world backup restore-as")
    @Description("Restores a backup as a new unloaded world")
    @CommandPermission("fancyworlds.commands.world.backup.restore")
    public void restoreAs(BukkitCommandActor actor, @SuggestWith(BackupNamesSuggestionProvider.class) String backupId, String newName) {
        send(actor, "progress", newName, backupId);
        backups.restore(backupId, newName).thenAccept(outcome -> sendResult(actor, outcome));
    }

    private void sendResult(BukkitCommandActor actor, WorldBackupService.Outcome outcome) {
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

        send(actor, key, outcome.worldName(), outcome.backupId());
    }

    private void send(BukkitCommandActor actor, String key, String worldName, String backupId) {
        FancyWorldsPlugin.get().getTranslator().translate("commands.world.backup." + key)
                .withPrefix().replace("worldName", worldName == null ? "" : worldName)
                .replace("backupId", backupId == null ? "" : backupId)
                .send(actor.sender());
    }

    static class BackupNamesSuggestionProvider implements SuggestionProvider<BukkitCommandActor> {

        @Override
        public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
            try {
                return FancyWorldsPlugin.get().getBackupService()
                        .list()
                        .stream()
                        .map(BackupArchive.Manifest::id)
                        .toList();
            } catch (IOException ex) {
                return List.of();
            }
        }
    }
}
