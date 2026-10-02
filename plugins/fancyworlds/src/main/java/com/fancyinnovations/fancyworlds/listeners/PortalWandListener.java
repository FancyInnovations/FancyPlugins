package com.fancyinnovations.fancyworlds.listeners;

import com.fancyinnovations.fancyworlds.portals.PortalWand;
import com.fancyinnovations.fancyworlds.portals.selection.PortalSelection;
import com.fancyinnovations.fancyworlds.portals.selection.PortalSelectionManager;
import com.fancyinnovations.fancyworlds.portals.selection.PortalSelectionPoint;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PortalWandListener extends FancyContext implements Listener {

    private final PortalSelectionManager selectionManager;
    private final PortalWand portalWand;

    public PortalWandListener(PortalSelectionManager selectionManager, PortalWand portalWand) {
        this.selectionManager = selectionManager;
        this.portalWand = portalWand;
    }

    @EventHandler
    public void onWandUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || !portalWand.isWand(event.getItem())) {
            return;
        }

        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        event.setCancelled(true);
        if (!event.getPlayer().hasPermission("fancyworlds.commands.portal.create")) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        PortalSelectionPoint point = PortalSelectionPoint.fromBlock(block);
        PortalSelection selection;
        String message;
        if (action == Action.LEFT_CLICK_BLOCK) {
            selection = selectionManager.setFirst(event.getPlayer().getUniqueId(), point);
            message = "commands.portal.selection.first";
        } else {
            selection = selectionManager.setSecond(event.getPlayer().getUniqueId(), point);
            message = "commands.portal.selection.second";
        }

        translator.translate(message)
                .withPrefix()
                .replace("worldName", point.worldName())
                .replace("x", String.valueOf(point.position().x()))
                .replace("y", String.valueOf(point.position().y()))
                .replace("z", String.valueOf(point.position().z()))
                .send(event.getPlayer());

        if (selection.isComplete() && selection.isInOneWorld()) {
            translator.translate("commands.portal.selection.complete")
                    .withPrefix()
                    .replace("volume", String.valueOf(selection.getVolume()))
                    .send(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        selectionManager.clearSelection(event.getPlayer().getUniqueId());
    }
}
