package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Piglin
 */
public class FS_PiglinData {

    /**
     * Use {@link Boolean} as value
     * True when immune to zombification
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor IMMUNE_TO_ZOMBIFICATION = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.piglin.AbstractPiglin", "DATA_IMMUNE_TO_ZOMBIFICATION");

    /**
     * Use {@link Boolean} as value
     * True when piglin is baby
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor IS_BABY = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.piglin.Piglin", "DATA_BABY_ID");

    /**
     * Use {@link Boolean} as value
     * True when piglin is charging crossbow
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor IS_CHARGING_CROSSBOW = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.piglin.Piglin", "DATA_IS_CHARGING_CROSSBOW");

    /**
     * Use {@link Boolean} as value
     * True when piglin is dancing
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor IS_DANCING = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.piglin.Piglin", "DATA_IS_DANCING");

}
