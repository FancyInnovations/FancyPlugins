package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Frog
 */
public class FS_FrogData {

    /**
     * Use Holder<FrogVariant> as value (requires NMS conversion)
     * Frog variant (TEMPERATE, WARM, COLD)
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor VARIANT = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.frog.Frog", "DATA_VARIANT_ID");

    /**
     * Use OptionalInt as value
     * Entity ID of the entity being targeted by tongue
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor TONGUE_TARGET = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.frog.Frog", "DATA_TONGUE_TARGET_ID");

}
