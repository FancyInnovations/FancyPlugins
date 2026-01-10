package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Bat
 */
public class FS_BatData {

    /**
     * Use {@link Byte} as value
     * Flag 0x01 = resting/hanging
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor FLAGS =
        new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.ambient.Bat", "DATA_ID_FLAGS");

    public static final byte FLAG_RESTING = 0x01;
}
