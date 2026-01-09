package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Warden
 */
public class FS_WardenData {

    /**
     * Use {@link Integer} as value
     * 0-150, affects visual anger state
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor ANGER_LEVEL =
        new FS_ClientboundSetEntityDataPacket.EntityDataAccessor("net.minecraft.world.entity.monster.warden.Warden", "CLIENT_ANGER_LEVEL");

    public static final int ANGER_CALM = 0;
    public static final int ANGER_AGITATED = 40;
    public static final int ANGER_ANGRY = 80;
    public static final int ANGER_MAX = 150;
}
