package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for TamableAnimal (extends Animal extends AgeableMob)
 * Used by: Wolf, Cat, Parrot
 */
public class FS_TamableAnimalData {

    /**
     * Use {@link Byte} as value
     * Bit 0x01 = Is sitting
     * Bit 0x02 = (unused)
     * Bit 0x04 = Is tamed
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor FLAGS = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.TamableAnimal", "DATA_FLAGS_ID");

    /**
     * Use {@link java.util.Optional}<{@link java.util.UUID}> as value
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor OWNER_UUID = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.TamableAnimal", "DATA_OWNERUUID_ID");

    // Flag bit constants for convenience
    public static final byte FLAG_SITTING = 0x01;
    public static final byte FLAG_TAMED = 0x04;

}
