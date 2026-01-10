package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Zombie
 */
public class FS_ZombieData {

    /**
     * Use {@link Boolean} as value
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor BABY =
        new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.zombie.Zombie", "DATA_BABY_ID");

    /**
     * Use {@link Boolean} as value
     * True when converting to drowned
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor DROWNED_CONVERSION =
        new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.zombie.Zombie", "DATA_DROWNED_CONVERSION_ID");

    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor BABY_LEGACY =
        new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.Zombie", "DATA_BABY_ID");

    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor DROWNED_CONVERSION_LEGACY =
        new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.Zombie", "DATA_DROWNED_CONVERSION_ID");
}
