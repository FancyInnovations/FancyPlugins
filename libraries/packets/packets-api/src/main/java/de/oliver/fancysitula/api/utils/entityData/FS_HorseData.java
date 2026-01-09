package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Horse
 */
public class FS_HorseData {

    /**
     * Use {@link Integer} as value - combined variant and marking value
     * Formula: (marking << 8) | variant
     * Use createVariantData() helper method
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor VARIANT = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.equine.Horse", "DATA_ID_TYPE_VARIANT");

    // Variant constants (color)
    public static final int VARIANT_WHITE = 0;
    public static final int VARIANT_CREAMY = 1;
    public static final int VARIANT_CHESTNUT = 2;
    public static final int VARIANT_BROWN = 3;
    public static final int VARIANT_BLACK = 4;
    public static final int VARIANT_GRAY = 5;
    public static final int VARIANT_DARK_BROWN = 6;

    // Marking constants
    public static final int MARKING_NONE = 0;
    public static final int MARKING_WHITE = 1;
    public static final int MARKING_WHITE_FIELD = 2;
    public static final int MARKING_WHITE_DOTS = 3;
    public static final int MARKING_BLACK_DOTS = 4;

    /**
     * Create combined variant data from color and marking
     */
    public static int createVariantData(int variant, int marking) {
        return variant | (marking << 8);
    }

}
