package com.fancyinnovations.fancyworlds.dialogs;

import com.fancyinnovations.fancydialogs.api.data.DialogBodyData;
import com.fancyinnovations.fancydialogs.api.data.DialogButton;
import com.fancyinnovations.fancydialogs.api.data.inputs.DialogInputs;
import com.fancyinnovations.fancydialogs.api.dialogs.ConfirmationDialog;import com.fancyinnovations.fancyworlds.dialogs.WorldsDialogController.Choice;
import com.fancyinnovations.fancyworlds.dialogs.WorldsDialogController.Choice;
import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.main.FancyWorldsPlugin;
import com.fancyinnovations.fancyworlds.worlds.FWorldImpl;
import com.fancyinnovations.fancyworlds.worlds.service.WorldOperations;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.IOException;
import java.util.*;

/** World browsing and actions for existing worlds. */
final class WorldDialogs {

    private static final int PAGE_SIZE = 8;

    private final FancyWorldsPlugin plugin;
    private final WorldsDialogController controller;
    private final BackupDialogs backups;

    WorldDialogs(FancyWorldsPlugin plugin, WorldsDialogController controller, BackupDialogs backups) {
        this.plugin = plugin;
        this.controller = controller;
        this.backups = backups;
    }

    void openWorldList(Player player, int requestedPage) {
        if (!controller.allowed(player, "world.menu")) return;

        List<FWorld> worlds = plugin.getWorldService().getAllWorlds().stream()
                .sorted(Comparator.comparing(FWorld::isWorldLoaded)
                        .reversed()
                        .thenComparing(FWorld::getName, String.CASE_INSENSITIVE_ORDER)
                )
                .toList();

        int pages = Math.max(1, (worlds.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        int page = Math.clamp(requestedPage, 1, pages);

        List<DialogBodyData> body = new ArrayList<>();
        body.add(controller.line(controller.tr("world_list.count", "count", worlds.size(), "page", page, "pages", pages)));
        if (worlds.isEmpty()) {
            body.add(controller.line(controller.tr("world_list.empty")));
        }

        List<DialogButton> buttons = new ArrayList<>();
        Map<String, Choice> choices = new HashMap<>();
        for (FWorld world : worlds.subList((page - 1) * PAGE_SIZE, Math.min(page * PAGE_SIZE, worlds.size()))) {
            String label = controller.tr("world_list.entry", "name", world.getName(), "status", controller.tr(world.isWorldLoaded() ? "status.loaded" : "status.unloaded"));
            controller.add(buttons, choices, label, "world", world.getID().toString(), page);
        }

        if (page > 1) {
            controller.add(buttons, choices, controller.tr("common.previous"), "world_page", String.valueOf(page - 1), page);
        }
        if (page < pages) {
            controller.add(buttons, choices, controller.tr("common.next"), "world_page", String.valueOf(page + 1), page);
        }

        if (controller.allowed(player, "world.create")) {
            controller.add(buttons, choices, controller.tr("world_list.create"), "create_form", "", page);
        }

        controller.show(player, controller.tr("world_list.title"), body, DialogInputs.EMPTY, buttons, choices);
    }

    void openWorldDetail(Player player, String id, int page) {
        if (!controller.allowed(player, "world.menu")) return;

        FWorld world = plugin.getWorldService().getWorldByID(id);
        if (world == null) {
            controller.send(player, "common.world_not_found", "worldName", id);
            openWorldList(player, page);
            return;
        }

        List<DialogBodyData> body = new ArrayList<>();
        body.add(controller.line(controller.tr("world_detail.status", "status", controller.tr(world.isWorldLoaded() ? "status.loaded" : "status.unloaded"))));
        body.add(controller.line(controller.tr("world_detail.environment", "value", world.getEnvironment().name())));
        body.add(controller.line(controller.tr("world_detail.generator", "value", world.getGenerator())));
        body.add(controller.line(controller.tr("world_detail.structures", "value", controller.tr(world.canGenerateStructures() ? "common.yes_msg" : "common.no_msg"))));
        if (controller.allowed(player, "world.seed")) {
            body.add(controller.line(controller.tr("world_detail.seed", "value", world.getSeed())));
        }
        if (world.isWorldLoaded()) {
            World bukkitWorld = world.getBukkitWorld();
            body.add(controller.line(controller.tr("world_detail.counts", "players", bukkitWorld.getPlayerCount(), "entities", bukkitWorld.getEntityCount(), "chunks", bukkitWorld.getChunkCount())));
        }

        List<DialogButton> buttons = new ArrayList<>();
        Map<String, Choice> choices = new HashMap<>();
        if (world.isWorldLoaded()) {
            if (controller.allowed(player, "world.teleport")) {
                controller.add(buttons, choices, controller.tr("world_detail.teleport"), "world_teleport", id, page);
            }

            if (controller.allowed(player, "world.unload") && world.getBukkitWorld().getPlayerCount() == 0) {
                controller.add(buttons, choices, controller.tr("world_detail.unload"), "world_unload", id, page);
            }

            if (controller.allowed(player, "world.set_spawn") && player.getWorld().getName().equals(world.getName())) {
                controller.add(buttons, choices, controller.tr("world_detail.spawn"), "world_spawn", id, page);
            }
        } else {
            if (controller.allowed(player, "world.load")) {
                controller.add(buttons, choices, controller.tr("world_detail.load"), "world_load", id, page);
            }

            if (controller.allowed(player, "world.backup.create")) {
                controller.add(buttons, choices, controller.tr("world_detail.backup_create"), "world_backup_create", id, page);
            }

            if (controller.allowed(player, "world.delete")) {
                controller.add(buttons, choices, controller.tr("world_detail.delete"), "world_delete", id, page);
            }
        }

        if (controller.allowed(player, "world.backup.list")) {
            controller.add(buttons, choices, controller.tr("world_detail.backups"), "backup_list", id + ":1", page);
        }

        controller.add(buttons, choices, controller.tr("common.back"), "world_page", String.valueOf(page), page);

        controller.show(player, controller.tr("world_detail.title", "name", world.getName()), body, DialogInputs.EMPTY, buttons, choices);
    }

    void worldAction(Player player, Choice choice) {
        if (!controller.allowed(player, "world.menu")) {
            controller.send(player, "dialogs.common.action_unavailable");
            controller.discard(player);
            return;
        }

        FWorld world = plugin.getWorldService().getWorldByID(choice.target());
        if (world == null) {
            controller.send(player, "common.world_not_found", "worldName", choice.target());
            openWorldList(player, choice.page());
            return;
        }

        String name = world.getName();
        switch (choice.action()) {
            case "world_teleport" -> {
                if (!controller.allowed(player, "world.teleport") || !world.isWorldLoaded()) {
                    worldUnavailable(player, choice);
                    return;
                }

                controller.discard(player);

                player.teleportAsync(world.getBukkitWorld().getSpawnLocation(), PlayerTeleportEvent.TeleportCause.COMMAND)
                        .whenComplete((ok, error) -> Bukkit.getScheduler().runTask(plugin, () -> {
                            if (player.isOnline())
                                controller.send(player, error == null && Boolean.TRUE.equals(ok) ? "commands.world.teleport.success" : "commands.world.teleport.failed", "worldName", name, "playerName", player.getName());
                        }));
            }

            case "world_load" -> {
                if (!controller.allowed(player, "world.load") || world.isWorldLoaded() || !(world instanceof FWorldImpl impl) || !world.isWorldOnDisk()) {
                    worldUnavailable(player, choice);
                    return;
                }

                if (WorldOperations.load(impl)) {
                    controller.send(player, "commands.world.load.success", "worldName", name);
                } else {
                    controller.send(player, "commands.world.load.failed", "worldName", name);
                }

                openWorldDetail(player, choice.target(), choice.page());
            }
            case "world_unload" -> {
                if (!controller.allowed(player, "world.unload") || !world.isWorldLoaded() || world.getBukkitWorld().getPlayerCount() != 0 || !(world instanceof FWorldImpl impl)) {
                    worldUnavailable(player, choice);
                    return;
                }

                if (WorldOperations.unload(impl)) {
                    controller.send(player, "commands.world.unload.success", "worldName", name);
                } else {
                    controller.send(player, "commands.world.unload.failed", "worldName", name);
                }

                openWorldDetail(player, choice.target(), choice.page());
            }
            case "world_spawn" -> {
                if (!controller.allowed(player, "world.set_spawn") || !world.isWorldLoaded() || player.getWorld() != world.getBukkitWorld()) {
                    worldUnavailable(player, choice);
                    return;
                }

                WorldOperations.setSpawn(world, player.getLocation());
                controller.send(player, "commands.world.set_spawn.success", "worldName", name, "location", controller.position(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()));
                openWorldDetail(player, choice.target(), choice.page());
            }
            case "world_delete" -> {
                if (!controller.allowed(player, "world.delete") || world.isWorldLoaded()) {
                    worldUnavailable(player, choice);
                    return;
                }

                controller.discard(player);

                new ConfirmationDialog(controller.tr("world_detail.delete_question", "name", name))
                        .withTitle(controller.tr("world_detail.delete"))
                        .withOnConfirm(() -> Bukkit.getScheduler().runTask(plugin, () -> deleteWorld(player, choice)))
                        .withOnCancel(() -> Bukkit.getScheduler().runTask(plugin, () -> openWorldDetail(player, choice.target(), choice.page())))
                        .ask(player);
            }
            case "world_backup_create" -> {
                if (!controller.allowed(player, "world.backup.create") || world.isWorldLoaded()) {
                    worldUnavailable(player, choice);
                    return;
                }
                backups.createBackup(player, world, choice);
            }
            default -> {
            }
        }
    }

    private void worldUnavailable(Player player, Choice choice) {
        controller.send(player, "dialogs.common.action_unavailable");
        openWorldDetail(player, choice.target(), choice.page());
    }

    private void deleteWorld(Player player, Choice choice) {
        if (!player.isOnline()) return;

        if (!controller.allowed(player, "world.delete")) {
            controller.send(player, "dialogs.common.action_unavailable");
            openWorldList(player, choice.page());
            return;
        }

        FWorld world = plugin.getWorldService().getWorldByID(choice.target());
        if (world == null || world.isWorldLoaded()) {
            controller.send(player, "dialogs.common.action_unavailable");
            openWorldList(player, choice.page());
            return;
        }

        try {
            WorldOperations.delete(world);
            controller.send(player, "commands.world.delete.success", "worldName", world.getName());
        } catch (IOException ex) {
            controller.send(player, "commands.world.delete.failed", "worldName", world.getName());
        }

        openWorldList(player, choice.page());
    }
}
