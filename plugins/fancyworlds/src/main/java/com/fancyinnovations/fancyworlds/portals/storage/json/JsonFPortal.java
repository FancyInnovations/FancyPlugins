package com.fancyinnovations.fancyworlds.portals.storage.json;

import com.fancyinnovations.fancyworlds.api.portals.FPortal;
import com.fancyinnovations.fancyworlds.api.portals.PortalPosition;
import com.fancyinnovations.fancyworlds.portals.FPortalImpl;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public record JsonFPortal(
        UUID id,
        String name,
        @SerializedName("world") String worldName,
        @SerializedName("minimum_position") PortalPosition minimumPosition,
        @SerializedName("maximum_position") PortalPosition maximumPosition,
        @SerializedName("destination_world") String destinationWorldName
) {
    public static JsonFPortal fromFPortal(FPortal portal) {
        return new JsonFPortal(
                portal.getID(),
                portal.getName(),
                portal.getWorldName(),
                portal.getMinimumPosition(),
                portal.getMaximumPosition(),
                portal.getDestinationWorldName()
        );
    }

    public FPortal toFPortal() {
        return new FPortalImpl(
                id,
                name,
                worldName,
                minimumPosition,
                maximumPosition,
                destinationWorldName
        );
    }
}
