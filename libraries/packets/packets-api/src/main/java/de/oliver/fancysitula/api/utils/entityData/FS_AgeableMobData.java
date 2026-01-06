package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for AgeableMob (extends PathfinderMob extends Mob)
 */
public class FS_AgeableMobData {

    /**
     * Use {@link Boolean} as value
     * true = baby, false = adult
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor BABY = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.AgeableMob", "DATA_BABY_ID");

}
