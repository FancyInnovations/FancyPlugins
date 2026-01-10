package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Cat
 */
public class FS_CatData {

    /**
     * Use Holder<CatVariant> as value (requires NMS conversion)
     * Cat variant (tabby, black, red, siamese, british_shorthair, calico, persian, ragdoll, white, jellie, all_black)
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor VARIANT = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.feline.Cat", "DATA_VARIANT_ID");

    /**
     * Use {@link Boolean} as value
     * True when cat is lying down
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor IS_LYING = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.feline.Cat", "IS_LYING");

    /**
     * Use {@link Boolean} as value
     * True when cat is relaxed (not looking around)
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor RELAX_STATE_ONE = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.feline.Cat", "RELAX_STATE_ONE");

    /**
     * Use {@link Integer} as value
     * DyeColor ordinal for collar color (only visible when tamed)
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor COLLAR_COLOR = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.feline.Cat", "DATA_COLLAR_COLOR");

}
