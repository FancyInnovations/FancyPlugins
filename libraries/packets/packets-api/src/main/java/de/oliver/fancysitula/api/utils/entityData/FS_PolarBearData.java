package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Polar Bear
 */
public class FS_PolarBearData {

    /**
     * Use {@link Boolean} as value
     * True when standing on hind legs (aggressive pose)
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor STANDING =
        new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.polarbear.PolarBear", "DATA_STANDING_ID");
}
