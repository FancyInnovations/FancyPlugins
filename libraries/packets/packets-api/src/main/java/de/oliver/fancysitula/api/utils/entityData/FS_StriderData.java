package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Strider
 */
public class FS_StriderData {

    /**
     * Use {@link Boolean} as value - whether the strider has a saddle
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor SADDLE = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.Strider", "DATA_SADDLE_ID");

    /**
     * Use {@link Boolean} as value - whether the strider is cold/shaking
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor SUFFOCATING = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.Strider", "DATA_SUFFOCATING");

}
