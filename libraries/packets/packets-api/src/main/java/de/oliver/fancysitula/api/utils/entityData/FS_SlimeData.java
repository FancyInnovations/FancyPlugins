package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Slime and MagmaCube
 */
public class FS_SlimeData {

    /**
     * Use {@link Integer} as value
     * Size of the slime (1 = smallest, 2 = small, 4 = big)
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor SIZE = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.Slime", "ID_SIZE");

}
