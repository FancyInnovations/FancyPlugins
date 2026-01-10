package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Tropical Fish
 */
public class FS_TropicalFishData {

    /**
     * Use {@link Integer} as value - combined variant data
     * Contains: pattern size (small=0, large=1), pattern, body color, pattern color
     * Use createVariantData() helper method
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor VARIANT = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.fish.TropicalFish", "DATA_ID_TYPE_VARIANT");

    // Pattern constants (small variants: 0-5, large variants: 0-5)
    // Small fish (size=0)
    public static final int PATTERN_KOB = 0;        // small
    public static final int PATTERN_SUNSTREAK = 1;  // small
    public static final int PATTERN_SNOOPER = 2;    // small
    public static final int PATTERN_DASHER = 3;     // small
    public static final int PATTERN_BRINELY = 4;    // small
    public static final int PATTERN_SPOTTY = 5;     // small
    // Large fish (size=1)
    public static final int PATTERN_FLOPPER = 0;    // large
    public static final int PATTERN_STRIPEY = 1;    // large
    public static final int PATTERN_GLITTER = 2;    // large
    public static final int PATTERN_BLOCKFISH = 3;  // large
    public static final int PATTERN_BETTY = 4;      // large
    public static final int PATTERN_CLAYFISH = 5;   // large

    // Size constants
    public static final int SIZE_SMALL = 0;
    public static final int SIZE_LARGE = 1;

    // Color constants (DyeColor ordinals 0-15)
    public static final int COLOR_WHITE = 0;
    public static final int COLOR_ORANGE = 1;
    public static final int COLOR_MAGENTA = 2;
    public static final int COLOR_LIGHT_BLUE = 3;
    public static final int COLOR_YELLOW = 4;
    public static final int COLOR_LIME = 5;
    public static final int COLOR_PINK = 6;
    public static final int COLOR_GRAY = 7;
    public static final int COLOR_LIGHT_GRAY = 8;
    public static final int COLOR_CYAN = 9;
    public static final int COLOR_PURPLE = 10;
    public static final int COLOR_BLUE = 11;
    public static final int COLOR_BROWN = 12;
    public static final int COLOR_GREEN = 13;
    public static final int COLOR_RED = 14;
    public static final int COLOR_BLACK = 15;

    /**
     * Create combined variant data
     * @param size 0 for small, 1 for large
     * @param pattern pattern index (0-5)
     * @param bodyColor DyeColor ordinal (0-15)
     * @param patternColor DyeColor ordinal (0-15)
     */
    public static int createVariantData(int size, int pattern, int bodyColor, int patternColor) {
        return size | (pattern << 8) | (bodyColor << 16) | (patternColor << 24);
    }

    /**
     * Get pattern index from name, returns -1 for small, -2 for large (unknown)
     */
    public static int[] getPatternAndSize(String patternName) {
        return switch (patternName.toUpperCase()) {
            // Small fish
            case "KOB" -> new int[]{SIZE_SMALL, PATTERN_KOB};
            case "SUNSTREAK" -> new int[]{SIZE_SMALL, PATTERN_SUNSTREAK};
            case "SNOOPER" -> new int[]{SIZE_SMALL, PATTERN_SNOOPER};
            case "DASHER" -> new int[]{SIZE_SMALL, PATTERN_DASHER};
            case "BRINELY" -> new int[]{SIZE_SMALL, PATTERN_BRINELY};
            case "SPOTTY" -> new int[]{SIZE_SMALL, PATTERN_SPOTTY};
            // Large fish
            case "FLOPPER" -> new int[]{SIZE_LARGE, PATTERN_FLOPPER};
            case "STRIPEY" -> new int[]{SIZE_LARGE, PATTERN_STRIPEY};
            case "GLITTER" -> new int[]{SIZE_LARGE, PATTERN_GLITTER};
            case "BLOCKFISH" -> new int[]{SIZE_LARGE, PATTERN_BLOCKFISH};
            case "BETTY" -> new int[]{SIZE_LARGE, PATTERN_BETTY};
            case "CLAYFISH" -> new int[]{SIZE_LARGE, PATTERN_CLAYFISH};
            default -> new int[]{SIZE_SMALL, PATTERN_KOB};
        };
    }

}
