package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Shulker
 */
public class FS_ShulkerData {

    /**
     * Use {@link Byte} as value - DyeColor ordinal (0-15), or 16 for default/no color
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor COLOR = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.Shulker", "DATA_COLOR_ID");

    // Color constants (DyeColor ordinals)
    public static final byte COLOR_WHITE = 0;
    public static final byte COLOR_ORANGE = 1;
    public static final byte COLOR_MAGENTA = 2;
    public static final byte COLOR_LIGHT_BLUE = 3;
    public static final byte COLOR_YELLOW = 4;
    public static final byte COLOR_LIME = 5;
    public static final byte COLOR_PINK = 6;
    public static final byte COLOR_GRAY = 7;
    public static final byte COLOR_LIGHT_GRAY = 8;
    public static final byte COLOR_CYAN = 9;
    public static final byte COLOR_PURPLE = 10;
    public static final byte COLOR_BLUE = 11;
    public static final byte COLOR_BROWN = 12;
    public static final byte COLOR_GREEN = 13;
    public static final byte COLOR_RED = 14;
    public static final byte COLOR_BLACK = 15;
    public static final byte COLOR_DEFAULT = 16; // No color / default purple

}
