package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Panda
 */
public class FS_PandaData {

    /**
     * Use {@link Integer} as value
     * Unhappy counter (sneezing animation)
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor UNHAPPY_COUNTER = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.panda.Panda", "UNHAPPY_COUNTER");

    /**
     * Use {@link Integer} as value
     * Sneeze counter
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor SNEEZE_COUNTER = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.panda.Panda", "SNEEZE_COUNTER");

    /**
     * Use {@link Integer} as value
     * Eating counter
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor EAT_COUNTER = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.panda.Panda", "EAT_COUNTER");

    /**
     * Use {@link Byte} as value
     * Main gene - determines appearance
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor MAIN_GENE = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.panda.Panda", "MAIN_GENE_ID");

    /**
     * Use {@link Byte} as value
     * Hidden gene - affects behavior
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor HIDDEN_GENE = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.panda.Panda", "HIDDEN_GENE_ID");

    /**
     * Use {@link Byte} as value
     * Bit 0x02 = Is sneezing
     * Bit 0x04 = Is rolling
     * Bit 0x08 = Is sitting
     * Bit 0x10 = Is on back
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor FLAGS = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.panda.Panda", "DATA_ID_FLAGS");

    // Gene constants (ordinals)
    public static final byte GENE_NORMAL = 0;
    public static final byte GENE_LAZY = 1;
    public static final byte GENE_WORRIED = 2;
    public static final byte GENE_PLAYFUL = 3;
    public static final byte GENE_BROWN = 4;
    public static final byte GENE_WEAK = 5;
    public static final byte GENE_AGGRESSIVE = 6;

    // Flag bit constants
    public static final byte FLAG_SNEEZING = 0x02;
    public static final byte FLAG_ROLLING = 0x04;
    public static final byte FLAG_SITTING = 0x08;
    public static final byte FLAG_ON_BACK = 0x10;

}
