package de.oliver.fancysitula.api.packets;

public abstract class FS_ClientboundAnimatePacket extends FS_ClientboundPacket {

    public static final int SWING_MAIN_ARM = 0;
    public static final int TAKE_DAMAGE = 1;
    public static final int LEAVE_BED = 2;
    public static final int SWING_OFFHAND = 3;
    public static final int CRITICAL_HIT = 4;
    public static final int MAGIC_CRITICAL_HIT = 5;

    protected int entityId;
    protected int animationId;

    public FS_ClientboundAnimatePacket(int entityId, int animationId) {
        this.entityId = entityId;
        this.animationId = animationId;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getAnimationId() {
        return animationId;
    }

    public void setAnimationId(int animationId) {
        this.animationId = animationId;
    }
}
