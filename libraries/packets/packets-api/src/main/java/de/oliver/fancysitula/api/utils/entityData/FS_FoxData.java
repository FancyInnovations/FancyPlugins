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
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor VARIANT = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.fox.Fox", "DATA_TYPE_ID");

    /**
     * Use {@link Byte} as value
     * Bit 0x01 = Is sitting
     * Bit 0x04 = Is crouching
     * Bit 0x20 = Is sleeping
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor FLAGS = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.fox.Fox", "DATA_FLAGS_ID");

    // Variant constants
    public static final int VARIANT_RED = 0;
    public static final int VARIANT_SNOW = 1;

    // Flag bit constants (only poses that work with packet-based NPCs)
    public static final byte FLAG_SITTING = 0x01;
    public static final byte FLAG_CROUCHING = 0x04;
    public static final byte FLAG_SLEEPING = 0x20;

}
