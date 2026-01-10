package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket.EntityDataAccessor;

/**
 * Entity data accessors for Phantom.
 * Phantom was added in 1.13 (The Update Aquatic).
 */
public class FS_PhantomData {

    /**
     * Use {@link Integer} as value.
     * Controls the size/wingspan of the phantom.
     * Default is 0, larger values = bigger phantom.
     */
    public static final EntityDataAccessor SIZE = new EntityDataAccessor(
            "net.minecraft.world.entity.monster.Phantom",
            "ID_SIZE"
    );
}
