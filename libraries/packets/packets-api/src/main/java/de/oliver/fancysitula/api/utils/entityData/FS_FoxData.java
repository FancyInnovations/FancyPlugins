package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Fox
 */
public class FS_FoxData {

    /**
     * Use {@link Integer} as value
     * Variant ordinal: 0 = RED, 1 = SNOW
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor VARIANT = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.Fox", "DATA_TYPE_ID");

    /**
     * Use {@link Byte} as value
     * Bit 0x01 = Is sitting
     * Bit 0x02 = (unused)
     * Bit 0x04 = Is crouching
     * Bit 0x08 = Is interested (head tilted)
     * Bit 0x10 = Is pouncing
     * Bit 0x20 = Is sleeping
     * Bit 0x40 = Is faceplanted
     * Bit 0x80 = Is defending
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor FLAGS = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.Fox", "DATA_FLAGS_ID");

    // Variant constants
    public static final int VARIANT_RED = 0;
    public static final int VARIANT_SNOW = 1;

    // Flag bit constants
    public static final byte FLAG_SITTING = 0x01;
    public static final byte FLAG_CROUCHING = 0x04;
    public static final byte FLAG_INTERESTED = 0x08;
    public static final byte FLAG_POUNCING = 0x10;
    public static final byte FLAG_SLEEPING = 0x20;
    public static final byte FLAG_FACEPLANTED = 0x40;
    public static final byte FLAG_DEFENDING = (byte) 0x80;

}
