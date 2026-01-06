package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Camel
 */
public class FS_CamelData {

    /**
     * Use {@link Boolean} as value - whether the camel is dashing
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor DASH = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.camel.Camel", "DASH");

    /**
     * Use {@link Long} as value - last pose change tick
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor LAST_POSE_CHANGE_TICK = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.camel.Camel", "LAST_POSE_CHANGE_TICK");

}
