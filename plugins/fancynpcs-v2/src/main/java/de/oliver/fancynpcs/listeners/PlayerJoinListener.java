package de.oliver.fancynpcs.listeners;

import com.destroystokyo.paper.profile.ProfileProperty;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.api.skins.SkinData;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerJoinListener implements Listener {

    private final FancyNpcs plugin = FancyNpcs.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (Npc npc : plugin.getNpcManagerImpl().getAllNpcs()) {
            npc.getIsVisibleForPlayer().put(event.getPlayer().getUniqueId(), false);
            npc.getIsLookingAtPlayer().put(event.getPlayer().getUniqueId(), false);
            npc.getIsTeamCreated().put(event.getPlayer().getUniqueId(), false);
        }

        // don't spawn the npc for player if he just joined
        plugin.getVisibilityTracker().addJoinDelayPlayer(event.getPlayer().getUniqueId());
        plugin.getScheduler().runTaskLater(null, 20L * 2, () -> plugin.getVisibilityTracker().removeJoinDelayPlayer(event.getPlayer().getUniqueId()));

        if (!plugin.getFancyNpcConfig().isMuteVersionNotification() && event.getPlayer().hasPermission("FancyNpcs.admin")) {
            plugin.getScheduler().runTaskAsynchronously(
                    () -> plugin.getVersionConfig().checkVersionAndDisplay(event.getPlayer(), true)
            );

            playerCommandAsOpWarning(event.getPlayer());
        }

        for (ProfileProperty property : event.getPlayer().getPlayerProfile().getProperties()) {
            if (!property.getName().equals("textures")) {
                continue;
            }

            SkinData skinData = new SkinData(
                    event.getPlayer().getUniqueId().toString(),
                    SkinData.SkinVariant.AUTO,
                    property.getValue(),
                    property.getSignature()
            );

            plugin.getSkinManagerImpl().getMemCache().addSkin(skinData);
        }
    }

    private void playerCommandAsOpWarning(Player p) {
        List<String> affected = new ArrayList<>();

        for (Npc npc : plugin.getNpcManagerImpl().getAllNpcs()) {
            for (List<NpcAction.NpcActionData> actions : npc.getData().getActions().values()) {
                for (NpcAction.NpcActionData action : actions) {
                    if (action.action().getName().equalsIgnoreCase("player_command_as_op")) {
                        affected.add(npc.getData().getName());
                    }
                }
            }
        }

        if (affected.isEmpty()) {
            return;
        }

        plugin.getTranslator().translate("player_command_as_op_warning")
                .withPrefix()
                .send(p);

        plugin.getTranslator().translate("player_command_as_op_warning_affected")
                .withPrefix()
                .replace("affected_npcs", Strings.join(affected, ','))
                .send(p);
    }
}
