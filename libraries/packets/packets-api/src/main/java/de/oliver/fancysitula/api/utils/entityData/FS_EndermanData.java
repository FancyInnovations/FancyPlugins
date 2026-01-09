package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Enderman
 */
public class FS_EndermanData {

    /**
     * Use {@link Boolean} as value
     * True when aggressive (open mouth)
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor CREEPY =
        new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.EnderMan", "DATA_CREEPY");

    /**
     * Use {@link Boolean} as value
     * True when being stared at
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor STARED_AT =
        new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.EnderMan", "DATA_STARED_AT");
}
