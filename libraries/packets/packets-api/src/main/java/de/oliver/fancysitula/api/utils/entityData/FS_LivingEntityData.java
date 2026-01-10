package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for LivingEntity
 */
public class FS_LivingEntityData {

    /**
     * Use {@link Byte} as value
     * Bit 0x01 = is hand active
     * Bit 0x02 = active hand (0 = main hand, 1 = offhand)
     * Bit 0x04 = is in riptide spin attack
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor LIVING_ENTITY_FLAGS = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.LivingEntity", "DATA_LIVING_ENTITY_FLAGS");

    /**
     * Use {@link Float} as value
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor HEALTH = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.LivingEntity", "DATA_HEALTH_ID");

    /**
     * Use {@link Integer} as value
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor EFFECT_COLOR = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.LivingEntity", "DATA_EFFECT_COLOR_ID");

    /**
     * Use {@link Boolean} as value
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor EFFECT_AMBIENCE = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.LivingEntity", "DATA_EFFECT_AMBIENCE_ID");

    /**
     * Use {@link Integer} as value
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor ARROW_COUNT = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.LivingEntity", "DATA_ARROW_COUNT_ID");

    /**
     * Use {@link Integer} as value
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor STINGER_COUNT = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.LivingEntity", "DATA_STINGER_COUNT_ID");

}
