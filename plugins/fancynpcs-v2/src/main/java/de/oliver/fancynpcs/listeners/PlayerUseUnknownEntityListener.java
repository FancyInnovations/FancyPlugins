package de.oliver.fancynpcs.listeners;

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import de.oliver.fancynpcs.api.events.NpcPreInteractEvent;
import de.oliver.fancysitula.api.utils.ServerVersion;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerUseUnknownEntityListener implements Listener {

    @EventHandler
    public void onPlayerUseUnknownEntity(PlayerUseUnknownEntityEvent event) {
        Player player = event.getPlayer();
        Npc npc = FancyNpcs.getInstance().getNpcManagerImpl().getNpc(event.getEntityId());

        // Skipping entities that are not FancyNpcs' NPCs
        if (npc == null) return;

        // PlayerUseUnknownEntityEvent can optionally be ALSO called for OFF-HAND slot. Making sure to run logic only ONCE.
        if (event.getHand() != EquipmentSlot.HAND) return;

        ActionTrigger actionTrigger = event.isAttack() ? ActionTrigger.LEFT_CLICK : ActionTrigger.RIGHT_CLICK;

        // PlayerUseUnknownEntityEvent can be called multiple times for interactions that are NOT attacks, making sure to run logic only ONCE.
        // This check is only needed for versions equal or older than 1.21.11
        if (ServerVersion.getCurrentVersion().getProtocolVersion() < ServerVersion.v26_1_2.getProtocolVersion()) {
            if (event.isAttack() || event.getClickedRelativePosition() == null || npc.getData().getType() == EntityType.ARMOR_STAND) {
                if (!new NpcPreInteractEvent(npc, player, actionTrigger).callEvent()) {
                    return;
                }
                npc.interact(player, actionTrigger);
            }
        } else {
            if (!new NpcPreInteractEvent(npc, player, actionTrigger).callEvent()) {
                return;
            }
            npc.interact(player, actionTrigger);
        }
    }

}
