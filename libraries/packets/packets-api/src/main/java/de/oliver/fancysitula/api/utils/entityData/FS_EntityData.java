package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

public class FS_EntityData {

    /**
     * Use {@link Byte} as value
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor SHARED_FLAGS = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.Entity", "DATA_SHARED_FLAGS_ID");

    /**
     * Use {@link Integer} as value
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor AIR_SUPPLY = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.Entity", "DATA_AIR_SUPPLY_ID");

    /**
     * Use {@link java.util.Optional<net.kyori.adventure.text.Component>} as value
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor CUSTOM_NAME = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.Entity", "DATA_CUSTOM_NAME");

    /**
     * Use {@link Boolean} as value
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor CUSTOM_NAME_VISIBLE = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.Entity", "DATA_CUSTOM_NAME_VISIBLE");

    /**
     * Use {@link Boolean} as value
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor SILENT = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.Entity", "DATA_SILENT");

    /**
     * Use {@link Boolean} as value
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor NO_GRAVITY = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.Entity", "DATA_NO_GRAVITY");

    /**
     * Use {@link String} as value (Pose enum name).
     * Controls the entity's visual pose/posture.
     * Valid values: STANDING, FALL_FLYING, SLEEPING, SWIMMING, SPIN_ATTACK, SNEAKING,
     * LONG_JUMPING, DYING, CROAKING, USING_TONGUE, SITTING, ROARING, SNIFFING,
     * EMERGING, DIGGING, SLIDING, SHOOTING, INHALING
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor POSE = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.Entity", "DATA_POSE");

    // Pose enum ordinal values
    public static final int POSE_STANDING = 0;
    public static final int POSE_FALL_FLYING = 1;
    public static final int POSE_SLEEPING = 2;
    public static final int POSE_SWIMMING = 3;
    public static final int POSE_SPIN_ATTACK = 4;
    public static final int POSE_SNEAKING = 5;
    public static final int POSE_LONG_JUMPING = 6;
    public static final int POSE_DYING = 7;
    public static final int POSE_CROAKING = 8;
    public static final int POSE_USING_TONGUE = 9;
    public static final int POSE_SITTING = 10;
    public static final int POSE_ROARING = 11;
    public static final int POSE_SNIFFING = 12;
    public static final int POSE_EMERGING = 13;
    public static final int POSE_DIGGING = 14;
    public static final int POSE_SLIDING = 15;
    public static final int POSE_SHOOTING = 16;
    public static final int POSE_INHALING = 17;

    /**
     * Use {@link Integer} as value
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor TICKS_FROZEN = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.Entity", "DATA_TICKS_FROZEN");

}

