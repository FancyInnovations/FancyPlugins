package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket.EntityDataAccessor;

/**
 * Entity data accessors for Interaction entity.
 * Interaction was added in 1.19.4.
 */
public class FS_InteractionData {

    /**
     * Width of the interaction entity.
     * Value should be a Float.
     */
    public static final EntityDataAccessor WIDTH = new EntityDataAccessor(
            "net.minecraft.world.entity.Interaction",
            "DATA_WIDTH_ID"
    );

    /**
     * Height of the interaction entity.
     * Value should be a Float.
     */
    public static final EntityDataAccessor HEIGHT = new EntityDataAccessor(
            "net.minecraft.world.entity.Interaction",
            "DATA_HEIGHT_ID"
    );

    /**
     * Whether the interaction responds to attacks.
     * Value should be a Boolean.
     */
    public static final EntityDataAccessor RESPONSE = new EntityDataAccessor(
            "net.minecraft.world.entity.Interaction",
            "DATA_RESPONSE_ID"
    );
}
