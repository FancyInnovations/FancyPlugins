package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for ArmorStand
 */
public class FS_ArmorStandData {

    /**
     * Use {@link Byte} as value
     * Bit 0x01 = Is small
     * Bit 0x04 = Has arms
     * Bit 0x08 = No base plate
     * Bit 0x10 = Is marker (zero bounding box)
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor CLIENT_FLAGS = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.decoration.ArmorStand", "DATA_CLIENT_FLAGS");

    /**
     * Use Vector3f (org.joml.Vector3f) as value - head rotation (x, y, z) in degrees
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor HEAD_POSE = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.decoration.ArmorStand", "DATA_HEAD_POSE");

    /**
     * Use Vector3f (org.joml.Vector3f) as value - body rotation (x, y, z) in degrees
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor BODY_POSE = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.decoration.ArmorStand", "DATA_BODY_POSE");

    /**
     * Use Vector3f (org.joml.Vector3f) as value - left arm rotation (x, y, z) in degrees
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor LEFT_ARM_POSE = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.decoration.ArmorStand", "DATA_LEFT_ARM_POSE");

    /**
     * Use Vector3f (org.joml.Vector3f) as value - right arm rotation (x, y, z) in degrees
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor RIGHT_ARM_POSE = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.decoration.ArmorStand", "DATA_RIGHT_ARM_POSE");

    /**
     * Use Vector3f (org.joml.Vector3f) as value - left leg rotation (x, y, z) in degrees
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor LEFT_LEG_POSE = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.decoration.ArmorStand", "DATA_LEFT_LEG_POSE");

    /**
     * Use Vector3f (org.joml.Vector3f) as value - right leg rotation (x, y, z) in degrees
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor RIGHT_LEG_POSE = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.decoration.ArmorStand", "DATA_RIGHT_LEG_POSE");

    // Flag bit constants
    public static final byte FLAG_SMALL = 0x01;
    public static final byte FLAG_ARMS = 0x04;
    public static final byte FLAG_NO_BASEPLATE = 0x08;
    public static final byte FLAG_MARKER = 0x10;

}
