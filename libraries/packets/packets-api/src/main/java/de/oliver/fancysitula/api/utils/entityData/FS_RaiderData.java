package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket.EntityDataAccessor;

/**
 * Entity data accessors for Raider entities (all Illagers).
 * Applies to: Pillager, Vindicator, Evoker, Illusioner, Ravager, Witch.
 */
public class FS_RaiderData {

    /**
     * Whether the raider is celebrating.
     * Value should be a Boolean.
     */
    public static final EntityDataAccessor IS_CELEBRATING = new EntityDataAccessor(
            "net.minecraft.world.entity.raid.Raider",
            "IS_CELEBRATING"
    );
}
