package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket.EntityDataAccessor;

/**
 * Entity data accessors for Sniffer.
 * Sniffer was added in 1.20 (Trails & Tales).
 */
public class FS_SnifferData {

    public static final EntityDataAccessor STATE = new EntityDataAccessor(
            "net.minecraft.world.entity.animal.sniffer.Sniffer",
            "DATA_STATE"
    );

    // Sniffer states (passed as String, converted to enum in implementation)
    public static final String STATE_IDLING = "IDLING";
    public static final String STATE_FEELING_HAPPY = "FEELING_HAPPY";
    public static final String STATE_SCENTING = "SCENTING";
    public static final String STATE_SNIFFING = "SNIFFING";
    public static final String STATE_SEARCHING = "SEARCHING";
    public static final String STATE_DIGGING = "DIGGING";
    public static final String STATE_RISING = "RISING";
}
