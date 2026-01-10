package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Vex
 */
public class FS_VexData {

    /**
     * Use {@link Byte} as value - flags byte
     * Bit 0x01 = is charging/attacking
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor FLAGS = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.Vex", "DATA_FLAGS_ID");

    public static final byte FLAG_CHARGING = 0x01;

}
