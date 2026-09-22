package com.fancyinnovations.fancyworlds.api.portals;

/**
 * An immutable block position used to define a portal region.
 *
 * @param x the block x coordinate
 * @param y the block y coordinate
 * @param z the block z coordinate
 */
public record PortalPosition(int x, int y, int z) {
}
