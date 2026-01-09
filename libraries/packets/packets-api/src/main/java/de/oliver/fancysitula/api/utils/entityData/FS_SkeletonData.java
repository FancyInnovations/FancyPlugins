package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Skeleton
 */
public class FS_SkeletonData {

    /**
     * Use {@link Boolean} as value
     * True when converting to stray
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor STRAY_CONVERSION =
        new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.skeleton.Skeleton", "DATA_STRAY_CONVERSION_ID");

    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor STRAY_CONVERSION_LEGACY =
        new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.Skeleton", "DATA_STRAY_CONVERSION_ID");
}
