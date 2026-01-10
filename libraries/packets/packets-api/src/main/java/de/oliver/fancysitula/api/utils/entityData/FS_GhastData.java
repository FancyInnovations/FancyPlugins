package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Ghast
 */
public class FS_GhastData {

    /**
     * Use {@link Boolean} as value
     * True when charging fireball
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor IS_CHARGING =
        new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.Ghast", "DATA_IS_CHARGING");
}
