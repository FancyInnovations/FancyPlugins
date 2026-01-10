package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Pufferfish
 */
public class FS_PufferfishData {

    /**
     * Use {@link Integer} as value
     * 0 = small, 1 = medium, 2 = full
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor PUFF_STATE =
        new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.fish.Pufferfish", "PUFF_STATE");

    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor PUFF_STATE_LEGACY =
        new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.Pufferfish", "PUFF_STATE");

    public static final int STATE_SMALL = 0;
    public static final int STATE_MEDIUM = 1;
    public static final int STATE_FULL = 2;
}
