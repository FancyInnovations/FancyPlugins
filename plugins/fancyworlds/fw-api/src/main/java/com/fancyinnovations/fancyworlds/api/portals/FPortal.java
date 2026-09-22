package com.fancyinnovations.fancyworlds.api.portals;

import java.util.UUID;

/**
 * A named cuboid region that teleports players to a world's spawn point.
 */
public interface FPortal {

    UUID getID();

    String getName();

    String getWorldName();

    PortalPosition getMinimumPosition();

    PortalPosition getMaximumPosition();

    String getDestinationWorldName();

    void setDestinationWorldName(String destinationWorldName);

    default boolean contains(String worldName, int x, int y, int z) {
        if (!getWorldName().equalsIgnoreCase(worldName)) {
            return false;
        }

        PortalPosition minimum = getMinimumPosition();
        PortalPosition maximum = getMaximumPosition();
        return x >= minimum.x() && x <= maximum.x()
                && y >= minimum.y() && y <= maximum.y()
                && z >= minimum.z() && z <= maximum.z();
    }

    default boolean overlaps(FPortal other) {
        if (!getWorldName().equalsIgnoreCase(other.getWorldName())) {
            return false;
        }

        PortalPosition minimum = getMinimumPosition();
        PortalPosition maximum = getMaximumPosition();
        PortalPosition otherMinimum = other.getMinimumPosition();
        PortalPosition otherMaximum = other.getMaximumPosition();
        return minimum.x() <= otherMaximum.x() && maximum.x() >= otherMinimum.x()
                && minimum.y() <= otherMaximum.y() && maximum.y() >= otherMinimum.y()
                && minimum.z() <= otherMaximum.z() && maximum.z() >= otherMinimum.z();
    }

    default long getVolume() {
        PortalPosition minimum = getMinimumPosition();
        PortalPosition maximum = getMaximumPosition();
        return (long) (maximum.x() - minimum.x() + 1)
                * (maximum.y() - minimum.y() + 1)
                * (maximum.z() - minimum.z() + 1);
    }
}
