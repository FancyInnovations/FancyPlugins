package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Armadillo
 */
public class FS_ArmadilloData {

    /**
     * Use {@link String} as value: "idle", "rolling", "scared", "unrolling"
     * The packet implementation will convert to the NMS enum type.
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor STATE = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.armadillo.Armadillo", "DATA_STATE");

    // State constants (matching Armadillo.ArmadilloState enum names)
    public static final String STATE_IDLE = "IDLE";
    public static final String STATE_ROLLING = "ROLLING";
    public static final String STATE_SCARED = "SCARED";
    public static final String STATE_UNROLLING = "UNROLLING";

}
