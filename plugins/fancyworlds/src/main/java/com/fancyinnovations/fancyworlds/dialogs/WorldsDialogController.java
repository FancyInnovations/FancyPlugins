package com.fancyinnovations.fancyworlds.dialogs;

import com.fancyinnovations.fancydialogs.api.Dialog;
import com.fancyinnovations.fancydialogs.api.FancyDialogs;
import com.fancyinnovations.fancydialogs.api.data.DialogBodyData;
import com.fancyinnovations.fancydialogs.api.data.DialogButton;
import com.fancyinnovations.fancydialogs.api.data.DialogData;
import com.fancyinnovations.fancydialogs.api.data.inputs.DialogCheckbox;
import com.fancyinnovations.fancydialogs.api.data.inputs.DialogInputs;
import com.fancyinnovations.fancydialogs.api.data.inputs.DialogSelect;
import com.fancyinnovations.fancydialogs.api.data.inputs.DialogTextField;
import com.fancyinnovations.fancydialogs.api.dialogs.ConfirmationDialog;
import com.fancyinnovations.fancydialogs.api.events.DialogButtonClickedEvent;
import com.fancyinnovations.fancyworlds.api.portals.FPortal;
import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.main.FancyWorldsPlugin;
import com.fancyinnovations.fancyworlds.worlds.FWorldImpl;
import com.fancyinnovations.fancyworlds.worlds.service.WorldCreationService;
import com.fancyinnovations.fancyworlds.worlds.service.WorldOperations;
import de.oliver.fancylib.translations.message.Message;
import de.oliver.fancylib.translations.message.SimpleMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Player-specific, short-lived management dialogs. All state changes run on the server thread.
 */
public final class WorldsDialogController implements Listener {

    private static final int PAGE_SIZE = 8;
    private static final long MAX_AGE_MS = 5 * 60 * 1000L;
    private static final Pattern VALID_SEED = Pattern.compile("-?[0-9]+");
    private final FancyWorldsPlugin plugin;
    private final Map<UUID, Session> sessions = new HashMap<>();

    public WorldsDialogController(FancyWorldsPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getScheduler().runTaskTimer(plugin, this::cleanup, 20 * 60, 20 * 60);
    }

    public void shutdown() {
        for (Session session : sessions.values()) {
            FancyDialogs.get().getDialogRegistry().unregister(session.dialog().getId());
        }
        sessions.clear();
    }

    public void openWorldList(Player player, int requestedPage) {
        if (!allowed(player, "world.menu")) return;

        List<FWorld> worlds = plugin.getWorldService().getAllWorlds().stream()
                .sorted(Comparator.comparing(FWorld::isWorldLoaded)
                        .reversed()
                        .thenComparing(FWorld::getName, String.CASE_INSENSITIVE_ORDER)
                )
                .toList();

        int pages = Math.max(1, (worlds.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        int page = Math.clamp(requestedPage, 1, pages);

        List<DialogBodyData> body = new ArrayList<>();
        body.add(line(tr("world_list.count", "count", worlds.size(), "page", page, "pages", pages)));
        if (worlds.isEmpty()) {
            body.add(line(tr("world_list.empty")));
        }

        List<DialogButton> buttons = new ArrayList<>();
        Map<String, Choice> choices = new HashMap<>();
        for (FWorld world : worlds.subList((page - 1) * PAGE_SIZE, Math.min(page * PAGE_SIZE, worlds.size()))) {
            String label = tr("world_list.entry", "name", world.getName(), "status", tr(world.isWorldLoaded() ? "status.loaded" : "status.unloaded"));
            add(buttons, choices, label, "world", world.getID().toString(), page);
        }

        if (page > 1) {
            add(buttons, choices, tr("common.previous"), "world_page", String.valueOf(page - 1), page);
        }
        if (page < pages) {
            add(buttons, choices, tr("common.next"), "world_page", String.valueOf(page + 1), page);
        }

        if (allowed(player, "world.create")) {
            add(buttons, choices, tr("world_list.create"), "create_form", "", page);
        }

        show(player, tr("world_list.title"), body, DialogInputs.EMPTY, buttons, choices);
    }

    public void openPortalList(Player player, int requestedPage) {
        if (!allowed(player, "portal.menu")) return;

        List<FPortal> portals = plugin.getPortalService().getAllPortals().stream()
                .sorted(Comparator.comparing(FPortal::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        int pages = Math.max(1, (portals.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        int page = Math.clamp(requestedPage, 1, pages);

        List<DialogBodyData> body = new ArrayList<>();
        body.add(line(tr("portal_list.count", "count", portals.size(), "page", page, "pages", pages)));
        if (portals.isEmpty()) {
            body.add(line(tr("portal_list.empty")));
        }

        List<DialogButton> buttons = new ArrayList<>();
        Map<String, Choice> choices = new HashMap<>();
        for (FPortal portal : portals.subList((page - 1) * PAGE_SIZE, Math.min(page * PAGE_SIZE, portals.size()))) {
            String label = tr("portal_list.entry", "name", portal.getName(), "source", portal.getWorldName(), "destination", portal.getDestinationWorldName());

            if (allowed(player, "portal.info")) {
                add(buttons, choices, label, "portal", portal.getID().toString(), page);
            } else {
                body.add(line(label));
            }
        }
        if (page > 1) {
            add(buttons, choices, tr("common.previous"), "portal_page", String.valueOf(page - 1), page);
        }
        if (page < pages) {
            add(buttons, choices, tr("common.next"), "portal_page", String.valueOf(page + 1), page);
        }

        show(player, tr("portal_list.title"), body, DialogInputs.EMPTY, buttons, choices);
    }

    private void openWorldDetail(Player player, String id, int page) {
        if (!allowed(player, "world.menu")) return;

        FWorld world = plugin.getWorldService().getWorldByID(id);
        if (world == null) {
            send(player, "common.world_not_found", "worldName", id);
            openWorldList(player, page);
            return;
        }

        List<DialogBodyData> body = new ArrayList<>();
        body.add(line(tr("world_detail.status", "status", tr(world.isWorldLoaded() ? "status.loaded" : "status.unloaded"))));
        body.add(line(tr("world_detail.environment", "value", world.getEnvironment().name())));
        body.add(line(tr("world_detail.generator", "value", world.getGenerator())));
        body.add(line(tr("world_detail.structures", "value", tr(world.canGenerateStructures() ? "common.yes_msg" : "common.no_msg"))));
        if (allowed(player, "world.seed")) {
            body.add(line(tr("world_detail.seed", "value", world.getSeed())));
        }
        if (world.isWorldLoaded()) {
            World bukkitWorld = world.getBukkitWorld();
            body.add(line(tr("world_detail.counts", "players", bukkitWorld.getPlayerCount(), "entities", bukkitWorld.getEntityCount(), "chunks", bukkitWorld.getChunkCount())));
        }

        List<DialogButton> buttons = new ArrayList<>();
        Map<String, Choice> choices = new HashMap<>();
        if (world.isWorldLoaded()) {
            if (allowed(player, "world.teleport")) {
                add(buttons, choices, tr("world_detail.teleport"), "world_teleport", id, page);
            }

            if (allowed(player, "world.unload") && world.getBukkitWorld().getPlayerCount() == 0) {
                add(buttons, choices, tr("world_detail.unload"), "world_unload", id, page);
            }

            if (allowed(player, "world.set_spawn") && player.getWorld().getName().equals(world.getName())) {
                add(buttons, choices, tr("world_detail.spawn"), "world_spawn", id, page);
            }
        } else {
            if (allowed(player, "world.load")) {
                add(buttons, choices, tr("world_detail.load"), "world_load", id, page);
            }

            if (allowed(player, "world.delete")) {
                add(buttons, choices, tr("world_detail.delete"), "world_delete", id, page);
            }
        }

        add(buttons, choices, tr("common.back"), "world_page", String.valueOf(page), page);

        show(player, tr("world_detail.title", "name", world.getName()), body, DialogInputs.EMPTY, buttons, choices);
    }

    private void openPortalDetail(Player player, String id, int page) {
        if (!allowed(player, "portal.menu") || !allowed(player, "portal.info")) return;

        FPortal portal = plugin.getPortalService().getPortalByID(id);
        if (portal == null) {
            send(player, "common.portal_not_found", "portalName", id);
            openPortalList(player, page);
            return;
        }

        List<DialogBodyData> body = List.of(
                line(tr("portal_detail.source", "value", portal.getWorldName())),
                line(tr("portal_detail.bounds", "min", position(portal.getMinimumPosition()), "max", position(portal.getMaximumPosition()))),
                line(tr("portal_detail.volume", "value", portal.getVolume())),
                line(tr("portal_detail.destination", "value", portal.getDestinationWorldName()))
        );

        List<DialogButton> buttons = new ArrayList<>();
        Map<String, Choice> choices = new HashMap<>();
        DialogInputs inputs = DialogInputs.EMPTY;
        if (allowed(player, "portal.set_destination")) {
            List<DialogSelect.Entry> destinations = plugin.getWorldService().getAllWorlds().stream()
                    .sorted(Comparator.comparing(FWorld::getName, String.CASE_INSENSITIVE_ORDER))
                    .map(world -> new DialogSelect.Entry(world.getName(), world.getName(), world.getName().equals(portal.getDestinationWorldName())))
                    .toList();

            if (!destinations.isEmpty()) {
                inputs = new DialogInputs(List.of(), List.of(new DialogSelect("destination", tr("portal_detail.choose_destination"), 1, destinations, Map.of(), 220)), List.of());
                add(buttons, choices, tr("portal_detail.save_destination"), "portal_destination", id, page);
            }
        }

        if (allowed(player, "portal.delete")) {
            add(buttons, choices, tr("portal_detail.delete"), "portal_delete", id, page);
        }

        add(buttons, choices, tr("common.back"), "portal_page", String.valueOf(page), page);

        show(player, tr("portal_detail.title", "name", portal.getName()), body, inputs, buttons, choices);
    }

    private void openCreateForm(Player player, int page) {
        if (!allowed(player, "world.menu") || !allowed(player, "world.create")) return;

        List<DialogSelect.Entry> environments = Stream.of(World.Environment.values())
                .map(value -> new DialogSelect.Entry(value.name(), value.name(), value == World.Environment.NORMAL))
                .toList();

        List<DialogSelect.Entry> generators = List.of(
                new DialogSelect.Entry("default", "default", true), new DialogSelect.Entry("flat", "flat", false),
                new DialogSelect.Entry("amplified", "amplified", false), new DialogSelect.Entry("large_biomes", "large_biomes", false)
        );

        DialogInputs inputs = new DialogInputs(
                List.of(
                        new DialogTextField("name", tr("create.name"), 1, "", 64, 1, Map.of(), 220),
                        new DialogTextField("seed", tr("create.seed"), 2, "", 20, 1, Map.of(), 220)
                ),
                List.of(
                        new DialogSelect("environment", tr("create.environment"), 3, environments, Map.of(), 220),
                        new DialogSelect("generator", tr("create.generator"), 4, generators, Map.of(), 220)
                ),
                List.of(
                        new DialogCheckbox("structures", tr("create.structures"), 5, false, Map.of()))
        );

        List<DialogButton> buttons = new ArrayList<>();
        Map<String, Choice> choices = new HashMap<>();
        add(buttons, choices, tr("create.submit"), "create_submit", "", page);
        add(buttons, choices, tr("common.back"), "world_page", String.valueOf(page), page);

        show(player, tr("create.title"), List.of(line(tr("create.description"))), inputs, buttons, choices);
    }

    private void show(Player player, String title, List<DialogBodyData> body, DialogInputs inputs, List<DialogButton> buttons, Map<String, Choice> choices) {
        discard(player);

        add(buttons, choices, tr("common.close"), "close", "", 1);

        String id = "fw_ui_" + UUID.randomUUID();
        Dialog dialog = FancyDialogs.get().createDialog(new DialogData(id, title, true, body, inputs, buttons, null, 2));
        FancyDialogs.get().getDialogRegistry().register(dialog);

        sessions.put(player.getUniqueId(), new Session(dialog, Map.copyOf(choices), System.currentTimeMillis()));

        dialog.open(player);
    }

    private void add(List<DialogButton> buttons, Map<String, Choice> choices, String label, String action, String target, int page) {
        DialogButton button = new DialogButton(label, label, List.of(), Map.of(), 180);
        buttons.add(button);
        choices.put(button.id(), new Choice(action, target, page));
    }

    @EventHandler
    public void onClick(DialogButtonClickedEvent event) {
        if (!event.getDialogId().startsWith("fw_ui_")) return;
        UUID playerId = event.getPlayer().getUniqueId();
        String dialogId = event.getDialogId();
        String buttonId = event.getButtonId();
        Map<String, String> inputs = Map.copyOf(event.getPayload());

        // FancyDialogs emits this event before its own validation; wait for that handler to finish.
        Bukkit.getScheduler().runTask(plugin, () -> handleClick(playerId, dialogId, buttonId, inputs));
    }

    private void handleClick(UUID playerId, String dialogId, String buttonId, Map<String, String> inputs) {
        Player player = Bukkit.getPlayer(playerId);
        Session session = sessions.get(playerId);
        if (player == null || session == null || !session.dialog().getId().equals(dialogId) || !session.dialog().isOpenedFor(player) || expired(session))
            return;

        Choice choice = session.choices().get(buttonId);
        if (choice == null || session.dialog().getData().getButtonById(buttonId) == null) {
            return;
        }

        switch (choice.action()) {
            case "world_page" -> openWorldList(player, Integer.parseInt(choice.target()));
            case "portal_page" -> openPortalList(player, Integer.parseInt(choice.target()));
            case "world" -> openWorldDetail(player, choice.target(), choice.page());
            case "portal" -> openPortalDetail(player, choice.target(), choice.page());
            case "create_form" -> openCreateForm(player, choice.page());
            case "close" -> {
                session.dialog().close(player);
                discard(player);
            }
            case "create_submit" -> createWorld(player, choice.page(), inputs);
            case "world_teleport", "world_load", "world_unload", "world_spawn", "world_delete" ->
                    worldAction(player, choice);
            case "portal_destination", "portal_delete" -> portalAction(player, choice, inputs);
            default -> {
            }
        }
    }

    private void createWorld(Player player, int page, Map<String, String> inputs) {
        if (!allowed(player, "world.create") || !allowed(player, "world.menu")) return;

        String name = inputs.getOrDefault("name", "").trim();

        String seedText = inputs.getOrDefault("seed", "").trim();
        Long seed = null;
        if (!seedText.isEmpty()) {
            if (!VALID_SEED.matcher(seedText).matches()) {
                send(player, "dialogs.create.invalid_seed");
                openCreateForm(player, page);
                return;
            }
            try {
                seed = Long.parseLong(seedText);
            } catch (NumberFormatException ex) {
                send(player, "dialogs.create.invalid_seed");
                openCreateForm(player, page);
                return;
            }
        }

        World.Environment environment;
        try {
            environment = World.Environment.valueOf(inputs.getOrDefault("environment", "NORMAL"));
        } catch (IllegalArgumentException ex) {
            send(player, "dialogs.create.invalid_option");
            openCreateForm(player, page);
            return;
        }

        String generator = inputs.getOrDefault("generator", "default");
        if (!List.of("default", "flat", "amplified", "large_biomes").contains(generator)) {
            send(player, "dialogs.create.invalid_option");
            openCreateForm(player, page);
            return;
        }

        String structuresText = inputs.getOrDefault("structures", "false");
        if (!structuresText.equals("true") && !structuresText.equals("false")) {
            send(player, "dialogs.create.invalid_option");
            openCreateForm(player, page);
            return;
        }

        discard(player);

        WorldCreationService.Result result = WorldCreationService.create(name, seed, environment, generator, Boolean.parseBoolean(structuresText));
        String key = switch (result.status()) {
            case CREATED -> "commands.world.create.success";
            case INVALID_NAME -> "commands.world.create.invalid_name";
            case ALREADY_EXISTS -> "commands.world.create.already_exists";
            case DISK_EXISTS -> "commands.world.create.disk_exists";
            case FAILED -> "commands.world.create.failed";
        };

        send(player, key, "worldName", name);

        if (result.status() == WorldCreationService.Status.CREATED) {
            openWorldDetail(player, result.world().getID().toString(), page);
        } else {
            openCreateForm(player, page);
        }
    }

    private void worldAction(Player player, Choice choice) {
        if (!allowed(player, "world.menu")) {
            send(player, "dialogs.common.action_unavailable");
            discard(player);
            return;
        }

        FWorld world = plugin.getWorldService().getWorldByID(choice.target());
        if (world == null) {
            send(player, "common.world_not_found", "worldName", choice.target());
            openWorldList(player, choice.page());
            return;
        }

        String name = world.getName();
        switch (choice.action()) {
            case "world_teleport" -> {
                if (!allowed(player, "world.teleport") || !world.isWorldLoaded()) {
                    worldUnavailable(player, choice);
                    return;
                }

                discard(player);

                player.teleportAsync(world.getBukkitWorld().getSpawnLocation(), PlayerTeleportEvent.TeleportCause.COMMAND)
                        .whenComplete((ok, error) -> Bukkit.getScheduler().runTask(plugin, () -> {
                            if (player.isOnline())
                                send(player, error == null && Boolean.TRUE.equals(ok) ? "commands.world.teleport.success" : "commands.world.teleport.failed", "worldName", name, "playerName", player.getName());
                        }));
            }

            case "world_load" -> {
                if (!allowed(player, "world.load") || world.isWorldLoaded() || !(world instanceof FWorldImpl impl) || !world.isWorldOnDisk()) {
                    worldUnavailable(player, choice);
                    return;
                }

                if (WorldOperations.load(impl)) {
                    send(player, "commands.world.load.success", "worldName", name);
                } else {
                    send(player, "commands.world.load.failed", "worldName", name);
                }

                openWorldDetail(player, choice.target(), choice.page());
            }
            case "world_unload" -> {
                if (!allowed(player, "world.unload") || !world.isWorldLoaded() || world.getBukkitWorld().getPlayerCount() != 0 || !(world instanceof FWorldImpl impl)) {
                    worldUnavailable(player, choice);
                    return;
                }

                if (WorldOperations.unload(impl)) {
                    send(player, "commands.world.unload.success", "worldName", name);
                } else {
                    send(player, "commands.world.unload.failed", "worldName", name);
                }

                openWorldDetail(player, choice.target(), choice.page());
            }
            case "world_spawn" -> {
                if (!allowed(player, "world.set_spawn") || !world.isWorldLoaded() || player.getWorld() != world.getBukkitWorld()) {
                    worldUnavailable(player, choice);
                    return;
                }

                WorldOperations.setSpawn(world, player.getLocation());
                send(player, "commands.world.set_spawn.success", "worldName", name, "location", position(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()));
                openWorldDetail(player, choice.target(), choice.page());
            }
            case "world_delete" -> {
                if (!allowed(player, "world.delete") || world.isWorldLoaded()) {
                    worldUnavailable(player, choice);
                    return;
                }

                discard(player);

                new ConfirmationDialog(tr("world_detail.delete_question", "name", name))
                        .withTitle(tr("world_detail.delete"))
                        .withOnConfirm(() -> Bukkit.getScheduler().runTask(plugin, () -> deleteWorld(player, choice)))
                        .withOnCancel(() -> Bukkit.getScheduler().runTask(plugin, () -> openWorldDetail(player, choice.target(), choice.page())))
                        .ask(player);
            }
            default -> {
            }
        }
    }

    private void worldUnavailable(Player player, Choice choice) {
        send(player, "dialogs.common.action_unavailable");
        openWorldDetail(player, choice.target(), choice.page());
    }

    private void deleteWorld(Player player, Choice choice) {
        if (!player.isOnline()) return;

        if (!allowed(player, "world.delete")) {
            send(player, "dialogs.common.action_unavailable");
            openWorldList(player, choice.page());
            return;
        }

        FWorld world = plugin.getWorldService().getWorldByID(choice.target());
        if (world == null || world.isWorldLoaded()) {
            send(player, "dialogs.common.action_unavailable");
            openWorldList(player, choice.page());
            return;
        }

        try {
            WorldOperations.delete(world);
            send(player, "commands.world.delete.success", "worldName", world.getName());
        } catch (IOException ex) {
            send(player, "commands.world.delete.failed", "worldName", world.getName());
        }

        openWorldList(player, choice.page());
    }

    private void portalAction(Player player, Choice choice, Map<String, String> inputs) {
        if (!allowed(player, "portal.menu")) {
            send(player, "dialogs.common.action_unavailable");
            discard(player);
            return;
        }

        FPortal portal = plugin.getPortalService().getPortalByID(choice.target());
        if (portal == null) {
            send(player, "common.portal_not_found", "portalName", choice.target());
            openPortalList(player, choice.page());
            return;
        }

        if (choice.action().equals("portal_destination")) {
            if (!allowed(player, "portal.set_destination")) {
                portalUnavailable(player, choice);
                return;
            }

            String destination = inputs.get("destination");
            FWorld target = destination == null ? null : plugin.getWorldService().getWorldByName(destination);
            if (target == null) {
                send(player, "dialogs.portal_detail.invalid_destination");
                openPortalDetail(player, choice.target(), choice.page());
                return;
            }

            portal.setDestinationWorldName(target.getName());

            plugin.getPortalService().updatePortal(portal);
            send(player, "commands.portal.set_destination.success", "portalName", portal.getName(), "destinationWorld", target.getName());

            openPortalDetail(player, choice.target(), choice.page());
        } else if (choice.action().equals("portal_delete")) {
            if (!allowed(player, "portal.delete")) {
                portalUnavailable(player, choice);
                return;
            }

            discard(player);

            new ConfirmationDialog(tr("portal_detail.delete_question", "name", portal.getName()))
                    .withTitle(tr("portal_detail.delete"))
                    .withOnConfirm(() -> Bukkit.getScheduler().runTask(plugin, () -> {
                        if (!player.isOnline()) return;

                        if (!allowed(player, "portal.delete")) {
                            send(player, "dialogs.common.action_unavailable");
                            openPortalList(player, choice.page());
                            return;
                        }

                        FPortal current = plugin.getPortalService().getPortalByID(choice.target());
                        if (current != null) {
                            plugin.getPortalService().unregisterPortal(current);
                            send(player, "commands.portal.delete.success", "portalName", current.getName());
                        }

                        openPortalList(player, choice.page());
                    }))
                    .withOnCancel(() -> Bukkit.getScheduler().runTask(plugin, () -> openPortalDetail(player, choice.target(), choice.page())))
                    .ask(player);
        }
    }

    private void portalUnavailable(Player player, Choice choice) {
        send(player, "dialogs.common.action_unavailable");
        openPortalDetail(player, choice.target(), choice.page());
    }

    private boolean allowed(Player player, String suffix) {
        return player.hasPermission("fancyworlds.commands." + suffix);
    }

    private String tr(String key, Object... replacements) {
        SimpleMessage message = (SimpleMessage) plugin.getTranslator().translate("dialogs." + key);
        for (int i = 0; i + 1 < replacements.length; i += 2)
            message.replace(String.valueOf(replacements[i]), String.valueOf(replacements[i + 1]));
        return message.getMessage();
    }

    private void send(Player player, String key, Object... replacements) {
        Message message = plugin.getTranslator().translate(key).withPrefix();

        for (int i = 0; i + 1 < replacements.length; i += 2) {
            message.replace(String.valueOf(replacements[i]), String.valueOf(replacements[i + 1]));
        }

        message.send(player);
    }

    private DialogBodyData line(String text) {
        return new DialogBodyData(text, 300);
    }

    private String position(com.fancyinnovations.fancyworlds.api.portals.PortalPosition point) {
        return position(point.x(), point.y(), point.z());
    }

    private String position(int x, int y, int z) {
        return x + ", " + y + ", " + z;
    }

    private void discard(Player player) {
        Session previous = sessions.remove(player.getUniqueId());

        if (previous != null) {
            FancyDialogs.get().getDialogRegistry().unregister(previous.dialog().getId());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        discard(event.getPlayer());
    }

    private boolean expired(Session session) {
        return System.currentTimeMillis() - session.openedAt() > MAX_AGE_MS;
    }

    private void cleanup() {
        sessions.entrySet().removeIf(entry -> {
            Player player = Bukkit.getPlayer(entry.getKey());

            Session session = entry.getValue();

            if (player != null && !expired(session) && session.dialog().isOpenedFor(player)) {
                return false;
            }

            FancyDialogs.get().getDialogRegistry().unregister(session.dialog().getId());

            return true;
        });
    }

    private record Choice(String action, String target, int page) {
    }

    private record Session(Dialog dialog, Map<String, Choice> choices, long openedAt) {
    }
}
