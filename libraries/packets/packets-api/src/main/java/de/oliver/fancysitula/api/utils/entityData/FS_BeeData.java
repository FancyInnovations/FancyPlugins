package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Bee
 */
public class FS_BeeData {

    /**
     * Use {@link Byte} as value
     * Bit 0x02 = Is angry
     * Bit 0x04 = Has stung
     * Bit 0x08 = Has nectar
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor FLAGS = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.bee.Bee", "DATA_FLAGS_ID");

    /**
     * Use {@link Integer} as value
     * Remaining anger time in ticks
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor REMAINING_ANGER_TIME = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.bee.Bee", "DATA_REMAINING_ANGER_TIME");

    // Flag bit constants
    public static final byte FLAG_ANGRY = 0x02;
    public static final byte FLAG_HAS_STUNG = 0x04;
    public static final byte FLAG_HAS_NECTAR = 0x08;

}
