package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Pig
 */
public class FS_PigData {

    /**
     * Use {@link Boolean} as value
     * True when pig has a saddle
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor SADDLE = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.Pig", "DATA_SADDLE_ID");

    /**
     * Use {@link Integer} as value
     * Boost time remaining in ticks (for carrot on a stick)
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor BOOST_TIME = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.Pig", "DATA_BOOST_TIME");

}
