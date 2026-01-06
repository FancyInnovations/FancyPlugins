package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Axolotl
 */
public class FS_AxolotlData {

    /**
     * Use {@link Integer} as value
     * Variant ordinal: 0 = LUCY (pink), 1 = WILD (brown), 2 = GOLD, 3 = CYAN, 4 = BLUE
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor VARIANT = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.axolotl.Axolotl", "DATA_VARIANT");

    /**
     * Use {@link Boolean} as value
     * True when axolotl is playing dead
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor PLAYING_DEAD = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.axolotl.Axolotl", "DATA_PLAYING_DEAD");

    /**
     * Use {@link Boolean} as value
     * True when spawned from bucket
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor FROM_BUCKET = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.axolotl.Axolotl", "FROM_BUCKET");

    // Variant constants
    public static final int VARIANT_LUCY = 0;   // Pink
    public static final int VARIANT_WILD = 1;   // Brown
    public static final int VARIANT_GOLD = 2;
    public static final int VARIANT_CYAN = 3;
    public static final int VARIANT_BLUE = 4;

}
