package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Rabbit
 */
public class FS_RabbitData {

    /**
     * Use {@link Integer} as value
     * Rabbit variant type
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor VARIANT = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.rabbit.Rabbit", "DATA_TYPE_ID");

    // Variant constants
    public static final int VARIANT_BROWN = 0;
    public static final int VARIANT_WHITE = 1;
    public static final int VARIANT_BLACK = 2;
    public static final int VARIANT_WHITE_SPLOTCHED = 3;
    public static final int VARIANT_GOLD = 4;
    public static final int VARIANT_SALT = 5;
    public static final int VARIANT_EVIL = 99;  // The Killer Bunny

}
