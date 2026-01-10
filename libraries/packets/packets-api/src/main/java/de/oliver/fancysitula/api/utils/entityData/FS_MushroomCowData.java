package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket.EntityDataAccessor;

/**
 * Entity data accessors for MushroomCow (Mooshroom).
 */
public class FS_MushroomCowData {

    /**
     * Mooshroom type (MushroomCow.MushroomType enum).
     * Value should be "RED" or "BROWN".
     */
    public static final EntityDataAccessor TYPE = new EntityDataAccessor(
            "net.minecraft.world.entity.animal.cow.MushroomCow",
            "DATA_TYPE"
    );

    public static final String TYPE_RED = "RED";
    public static final String TYPE_BROWN = "BROWN";
}
