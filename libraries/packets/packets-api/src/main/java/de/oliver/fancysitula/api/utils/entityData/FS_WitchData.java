package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Witch
 */
public class FS_WitchData {

    /**
     * Use {@link Boolean} as value
     * True when drinking potion
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor USING_ITEM =
        new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.Witch", "DATA_USING_ITEM");
}
