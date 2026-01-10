package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Avatar entity (1.21.11+)
 * Avatar is the base class for Player and Mannequin, providing player-like appearance
 */
public class FS_AvatarData {

    /**
     * Use {@link Byte} as value
     * Skin layer visibility flags (same as Player)
     * Bit 0x01 = Cape
     * Bit 0x02 = Jacket
     * Bit 0x04 = Left Sleeve
     * Bit 0x08 = Right Sleeve
     * Bit 0x10 = Left Pants Leg
     * Bit 0x20 = Right Pants Leg
     * Bit 0x40 = Hat
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor DATA_PLAYER_MODE_CUSTOMISATION =
            new FS_ClientboundSetEntityDataPacket.EntityDataAccessor(
                    "net.minecraft.world.entity.Avatar",
                    "DATA_PLAYER_MODE_CUSTOMISATION"
            );

    /**
     * Use HumanoidArm enum as value (LEFT or RIGHT)
     * The main hand of the avatar
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor DATA_PLAYER_MAIN_HAND =
            new FS_ClientboundSetEntityDataPacket.EntityDataAccessor(
                    "net.minecraft.world.entity.Avatar",
                    "DATA_PLAYER_MAIN_HAND"
            );

    // Skin layer bit constants (same as FS_PlayerData for convenience)
    public static final byte SKIN_CAPE = 0x01;
    public static final byte SKIN_JACKET = 0x02;
    public static final byte SKIN_LEFT_SLEEVE = 0x04;
    public static final byte SKIN_RIGHT_SLEEVE = 0x08;
    public static final byte SKIN_LEFT_PANTS = 0x10;
    public static final byte SKIN_RIGHT_PANTS = 0x20;
    public static final byte SKIN_HAT = 0x40;
    public static final byte SKIN_ALL = 0x7F;

}
