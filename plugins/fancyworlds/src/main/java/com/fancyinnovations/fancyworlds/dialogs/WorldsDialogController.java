package com.fancyinnovations.fancyworlds.dialogs;

import com.fancyinnovations.fancydialogs.api.Dialog;
import com.fancyinnovations.fancydialogs.api.FancyDialogs;
import com.fancyinnovations.fancydialogs.api.data.DialogBodyData;
import com.fancyinnovations.fancydialogs.api.data.DialogButton;
import com.fancyinnovations.fancydialogs.api.data.DialogData;
import com.fancyinnovations.fancydialogs.api.data.inputs.DialogInputs;
import com.fancyinnovations.fancydialogs.api.events.DialogButtonClickedEvent;
import com.fancyinnovations.fancyworlds.api.portals.PortalPosition;
import com.fancyinnovations.fancyworlds.main.FancyWorldsPlugin;
import de.oliver.fancylib.translations.message.Message;
import de.oliver.fancylib.translations.message.SimpleMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

/**
 * Coordinates player-specific dialogs, click routing, and short-lived sessions.
 * All state changes run on the server thread.
 */
public final class WorldsDialogController implements Listener {

    private static final long MAX_AGE_MS = 5 * 60 * 1000L;
    private final FancyWorldsPlugin plugin;
    private final Map<UUID, Session> sessions = new HashMap<>();
    private final WorldDialogs worlds;
    private final WorldCreationDialog creation;
    private final BackupDialogs backups;
    private final PortalDialogs portals;

    public WorldsDialogController(FancyWorldsPlugin plugin) {
        this.plugin = plugin;
        this.backups = new BackupDialogs(plugin, this);
        this.worlds = new WorldDialogs(plugin, this, backups);
        this.creation = new WorldCreationDialog(this);
        this.portals = new PortalDialogs(plugin, this);
        Bukkit.getScheduler().runTaskTimer(plugin, this::cleanup, 20 * 60, 20 * 60);
    }

    public void openWorldList(Player player, int requestedPage) {
        worlds.openWorldList(player, requestedPage);
    }

    public void openPortalList(Player player, int requestedPage) {
        portals.openPortalList(player, requestedPage);
    }

    void openWorldDetail(Player player, String id, int page) {
        worlds.openWorldDetail(player, id, page);
    }

    public void shutdown() {
        for (Session session : sessions.values()) {
            FancyDialogs.get().getDialogRegistry().unregister(session.dialog().getId());
        }
        sessions.clear();
    }

    void show(Player player, String title, List<DialogBodyData> body, DialogInputs inputs, List<DialogButton> buttons, Map<String, Choice> choices) {
        discard(player);

        add(buttons, choices, tr("common.close"), "close", "", 1);

        String id = "fw_ui_" + UUID.randomUUID();
        Dialog dialog = FancyDialogs.get().createDialog(new DialogData(id, title, true, body, inputs, buttons, null, 2));
        FancyDialogs.get().getDialogRegistry().register(dialog);

        sessions.put(player.getUniqueId(), new Session(dialog, Map.copyOf(choices), System.currentTimeMillis()));

        dialog.open(player);
    }

    void add(List<DialogButton> buttons, Map<String, Choice> choices, String label, String action, String target, int page) {
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
            case "world" -> worlds.openWorldDetail(player, choice.target(), choice.page());
            case "backup_list" -> {
                String[] parts = choice.target().split(":", 2);
                backups.openBackupList(player, parts[0], Integer.parseInt(parts[1]), choice.page());
            }
            case "backup_detail" -> backups.openBackupDetail(player, choice.target(), choice.page());
            case "backup_replace" -> backups.confirmBackupReplace(player, choice);
            case "backup_restore_as" -> backups.restoreAsBackup(player, choice, inputs);
            case "portal" -> portals.openPortalDetail(player, choice.target(), choice.page());
            case "create_form" -> creation.openCreateForm(player, choice.page());
            case "close" -> {
                session.dialog().close(player);
                discard(player);
            }
            case "create_submit" -> creation.createWorld(player, choice.page(), inputs);
            case "world_teleport", "world_load", "world_unload", "world_spawn", "world_delete", "world_backup_create" ->
                    worlds.worldAction(player, choice);
            case "portal_destination", "portal_delete" -> portals.portalAction(player, choice, inputs);
            default -> {
            }
        }
    }

    boolean allowed(Player player, String suffix) {
        return player.hasPermission("fancyworlds.commands." + suffix);
    }

    String tr(String key, Object... replacements) {
        SimpleMessage message = (SimpleMessage) plugin.getTranslator().translate("dialogs." + key);
        for (int i = 0; i + 1 < replacements.length; i += 2)
            message.replace(String.valueOf(replacements[i]), String.valueOf(replacements[i + 1]));
        return message.getMessage();
    }

    void send(Player player, String key, Object... replacements) {
        Message message = plugin.getTranslator().translate(key).withPrefix();

        for (int i = 0; i + 1 < replacements.length; i += 2) {
            message.replace(String.valueOf(replacements[i]), String.valueOf(replacements[i + 1]));
        }

        message.send(player);
    }

    DialogBodyData line(String text) {
        return new DialogBodyData(text, 300);
    }

    String position(PortalPosition point) {
        return position(point.x(), point.y(), point.z());
    }

    String position(int x, int y, int z) {
        return x + ", " + y + ", " + z;
    }

    void discard(Player player) {
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

    record Choice(String action, String target, int page) {
    }

    private record Session(Dialog dialog, Map<String, Choice> choices, long openedAt) {
    }
}
