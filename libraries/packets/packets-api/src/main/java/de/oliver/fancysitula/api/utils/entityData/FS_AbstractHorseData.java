package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for AbstractHorse (Horse, Donkey, Mule, Camel, etc.)
 *
 * NOTE: Horses do NOT have a SADDLED flag in entity data. Saddles are controlled
 * via the EquipmentSlot.SADDLE equipment slot, not entity data flags.
 * To show a saddle on a horse, use the equipment packet with FS_EquipmentSlot.SADDLE.
 */
public class FS_AbstractHorseData {

    /**
     * Use {@link Byte} as value - horse flags
     * Bit flags: FLAG_TAMED (0x02), FLAG_BRED (0x08), FLAG_EATING (0x10), FLAG_STANDING (0x20), FLAG_MOUTH_OPEN (0x40)
     *
     * NOTE: There is NO FLAG_SADDLED - saddles are equipment, not entity data.
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor FLAGS = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.equine.AbstractHorse", "DATA_ID_FLAGS");

    // Flag constants (based on NMS AbstractHorse.java)
    public static final byte FLAG_TAMED = 0x02;
    // Note: 0x04 is NOT used - there is no FLAG_SADDLED in AbstractHorse
    public static final byte FLAG_BRED = 0x08;
    public static final byte FLAG_EATING = 0x10;
    public static final byte FLAG_STANDING = 0x20;  // Rearing
    public static final byte FLAG_MOUTH_OPEN = 0x40;

}
