package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Wolf
 */
public class FS_WolfData {

    /**
     * Use {@link Boolean} as value
     * True when wolf is begging (head tilted)
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor INTERESTED = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.wolf.Wolf", "DATA_INTERESTED_ID");

    /**
     * Use {@link Integer} as value
     * DyeColor ordinal for collar color (only visible when tamed)
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor COLLAR_COLOR = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.wolf.Wolf", "DATA_COLLAR_COLOR");

    /**
     * Use {@link Integer} as value
     * Remaining anger time in ticks (> 0 = angry, eyes turn red)
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor REMAINING_ANGER_TIME = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.wolf.Wolf", "DATA_REMAINING_ANGER_TIME");

    /**
     * Use Holder<WolfVariant> as value (requires NMS conversion)
     * Wolf variant (PALE, SPOTTED, SNOWY, BLACK, ASHEN, RUSTY, WOODS, CHESTNUT, STRIPED)
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor VARIANT = new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.animal.wolf.Wolf", "DATA_VARIANT_ID");

}
