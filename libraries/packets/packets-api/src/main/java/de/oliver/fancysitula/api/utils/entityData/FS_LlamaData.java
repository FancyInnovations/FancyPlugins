package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Llama and TraderLlama
 */
public class FS_LlamaData {

    /**
     * Use {@link Integer} as value - llama variant (0-3)
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor VARIANT = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.equine.Llama", "DATA_VARIANT_ID");

    // Variant constants
    public static final int VARIANT_CREAMY = 0;
    public static final int VARIANT_WHITE = 1;
    public static final int VARIANT_BROWN = 2;
    public static final int VARIANT_GRAY = 3;

}
