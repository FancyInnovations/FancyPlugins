package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket.EntityDataAccessor;

/**
 * Entity data accessors for Chicken.
 * Chicken variants were added in 1.21.5.
 */
public class FS_ChickenData {

    /**
     * Chicken variant (Holder<ChickenVariant>).
     * Value should be a String like "minecraft:temperate".
     * Added in 1.21.5.
     */
    public static final EntityDataAccessor VARIANT = new EntityDataAccessor(
            "net.minecraft.world.entity.animal.chicken.Chicken",
            "DATA_VARIANT_ID"
    );
}
