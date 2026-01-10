package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Snow Golem
 */
public class FS_SnowGolemData {

    /**
     * Use {@link Byte} as value
     * Flag 0x10 = has pumpkin head
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor PUMPKIN =
        new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.golem.SnowGolem", "DATA_PUMPKIN_ID");

    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor PUMPKIN_LEGACY =
        new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.SnowGolem", "DATA_PUMPKIN_ID");

    public static final byte FLAG_PUMPKIN = 0x10;
}
