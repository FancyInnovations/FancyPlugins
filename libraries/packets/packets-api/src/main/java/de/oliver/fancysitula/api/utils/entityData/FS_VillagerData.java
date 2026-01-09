package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Villager
 */
public class FS_VillagerData {

    /**
     * Use VillagerData as value (requires NMS conversion)
     * VillagerData contains: profession, type (biome), and level
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor VILLAGER_DATA = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.npc.Villager", "DATA_VILLAGER_DATA");

}
