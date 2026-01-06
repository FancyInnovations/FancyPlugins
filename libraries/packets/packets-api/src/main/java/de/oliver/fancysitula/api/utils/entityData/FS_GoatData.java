package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Goat
 */
public class FS_GoatData {

    /**
     * Use {@link Boolean} as value
     * True when goat is screaming variant
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor IS_SCREAMING = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.goat.Goat", "DATA_IS_SCREAMING_GOAT");

    /**
     * Use {@link Boolean} as value
     * True when goat has its left horn
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor HAS_LEFT_HORN = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.goat.Goat", "DATA_HAS_LEFT_HORN");

    /**
     * Use {@link Boolean} as value
     * True when goat has its right horn
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor HAS_RIGHT_HORN = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.goat.Goat", "DATA_HAS_RIGHT_HORN");

}
