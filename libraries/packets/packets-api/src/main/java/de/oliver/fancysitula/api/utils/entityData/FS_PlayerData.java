package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Player entity
 */
public class FS_PlayerData {

    /**
     * Use {@link Float} as value
     * Additional hearts from Absorption effect
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor ABSORPTION = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.player.Player", "DATA_PLAYER_ABSORPTION_ID");

    /**
     * Use {@link Integer} as value
     * Score displayed on death screen
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor SCORE = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.player.Player", "DATA_SCORE_ID");

    /**
     * Use {@link Byte} as value
     * Skin layer visibility flags
     * Bit 0x01 = Cape
     * Bit 0x02 = Jacket
     * Bit 0x04 = Left Sleeve
     * Bit 0x08 = Right Sleeve
     * Bit 0x10 = Left Pants Leg
     * Bit 0x20 = Right Pants Leg
     * Bit 0x40 = Hat
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor SKIN_CUSTOMIZATION = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.player.Player", "DATA_PLAYER_MODE_CUSTOMISATION");

    /**
     * Use {@link Byte} as value
     * 0 = Left hand, 1 = Right hand
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor MAIN_HAND = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.player.Player", "DATA_PLAYER_MAIN_HAND");

    /**
     * Use CompoundTag as value (NBT for left shoulder entity)
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor SHOULDER_LEFT = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.player.Player", "DATA_SHOULDER_LEFT");

    /**
     * Use CompoundTag as value (NBT for right shoulder entity)
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor SHOULDER_RIGHT = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.player.Player", "DATA_SHOULDER_RIGHT");

    // Skin layer bit constants
    public static final byte SKIN_CAPE = 0x01;
    public static final byte SKIN_JACKET = 0x02;
    public static final byte SKIN_LEFT_SLEEVE = 0x04;
    public static final byte SKIN_RIGHT_SLEEVE = 0x08;
    public static final byte SKIN_LEFT_PANTS = 0x10;
    public static final byte SKIN_RIGHT_PANTS = 0x20;
    public static final byte SKIN_HAT = 0x40;
    public static final byte SKIN_ALL = 0x7F;

}
