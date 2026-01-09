package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Salmon
 */
public class FS_SalmonData {

    /**
     * Use {@link Integer} as value
     * 0 = small, 1 = medium, 2 = large (1.21.4+)
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor SIZE =
        new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.fish.Salmon", "DATA_TYPE");

    public static final int SIZE_SMALL = 0;
    public static final int SIZE_MEDIUM = 1;
    public static final int SIZE_LARGE = 2;
}
