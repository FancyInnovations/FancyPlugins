package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Guardian
 */
public class FS_GuardianData {

    /**
     * Use {@link Boolean} as value
     * True = moving (spikes retracted), false = still (spikes out)
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor MOVING =
        new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.Guardian", "DATA_ID_MOVING");

    /**
     * Use {@link Integer} as value
     * Entity ID of laser target, 0 for none
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor ATTACK_TARGET =
        new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.Guardian", "DATA_ID_ATTACK_TARGET");
}
