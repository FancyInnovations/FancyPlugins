package com.fancyinnovations.fancyworlds.portals;

import com.fancyinnovations.fancyworlds.api.portals.FPortal;
import com.fancyinnovations.fancyworlds.api.portals.PortalPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class FPortalImpl implements FPortal {

    private final UUID id;
    private final String name;
    private final String worldName;
    private final PortalPosition minimumPosition;
    private final PortalPosition maximumPosition;
    private String destinationWorldName;

    public FPortalImpl(
            @Nullable UUID id,
            @NotNull String name,
            @NotNull String worldName,
            @NotNull PortalPosition firstPosition,
            @NotNull PortalPosition secondPosition,
            @NotNull String destinationWorldName
    ) {
        this.id = id != null ? id : UUID.randomUUID();
        this.name = name;
        this.worldName = worldName;
        this.minimumPosition = new PortalPosition(
                Math.min(firstPosition.x(), secondPosition.x()),
                Math.min(firstPosition.y(), secondPosition.y()),
                Math.min(firstPosition.z(), secondPosition.z())
        );
        this.maximumPosition = new PortalPosition(
                Math.max(firstPosition.x(), secondPosition.x()),
                Math.max(firstPosition.y(), secondPosition.y()),
                Math.max(firstPosition.z(), secondPosition.z())
        );
        this.destinationWorldName = destinationWorldName;
    }

    @Override
    public UUID getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getWorldName() {
        return worldName;
    }

    @Override
    public PortalPosition getMinimumPosition() {
        return minimumPosition;
    }

    @Override
    public PortalPosition getMaximumPosition() {
        return maximumPosition;
    }

    @Override
    public String getDestinationWorldName() {
        return destinationWorldName;
    }

    @Override
    public void setDestinationWorldName(@NotNull String destinationWorldName) {
        this.destinationWorldName = destinationWorldName;
    }
}
