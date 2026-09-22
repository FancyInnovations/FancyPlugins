package com.fancyinnovations.fancyworlds.portals.selection;

import com.fancyinnovations.fancyworlds.api.portals.PortalPosition;
import org.bukkit.block.Block;

public record PortalSelectionPoint(String worldName, PortalPosition position) {

    public static PortalSelectionPoint fromBlock(Block block) {
        return new PortalSelectionPoint(
                block.getWorld().getName(),
                new PortalPosition(block.getX(), block.getY(), block.getZ())
        );
    }
}
