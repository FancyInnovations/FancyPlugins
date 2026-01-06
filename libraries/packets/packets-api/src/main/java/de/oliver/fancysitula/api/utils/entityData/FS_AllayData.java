package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Allay
 */
public class FS_AllayData {

    /**
     * Use {@link Boolean} as value - whether the allay is dancing
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor DANCING = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.allay.Allay", "DATA_DANCING");

    /**
     * Use {@link Boolean} as value - whether the allay can duplicate
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor CAN_DUPLICATE = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.allay.Allay", "DATA_CAN_DUPLICATE");

}
