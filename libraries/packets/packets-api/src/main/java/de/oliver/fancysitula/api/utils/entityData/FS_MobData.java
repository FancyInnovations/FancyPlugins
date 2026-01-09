package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Mob (extends LivingEntity)
 */
public class FS_MobData {

    /**
     * Use {@link Byte} as value
     * Bit 0x01 = NoAI
     * Bit 0x02 = Left-handed
     * Bit 0x04 = Aggressive
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor MOB_FLAGS = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.Mob", "DATA_MOB_FLAGS_ID");

}
