package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Creeper
 */
public class FS_CreeperData {

    /**
     * Use {@link Integer} as value
     * -1 = idle, positive = swelling countdown
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor SWELL_DIR = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.Creeper", "DATA_SWELL_DIR");

    /**
     * Use {@link Boolean} as value
     * True when powered (struck by lightning)
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor IS_POWERED = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.Creeper", "DATA_IS_POWERED");

    /**
     * Use {@link Boolean} as value
     * True when ignited (flint and steel)
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor IS_IGNITED = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.Creeper", "DATA_IS_IGNITED");

}
