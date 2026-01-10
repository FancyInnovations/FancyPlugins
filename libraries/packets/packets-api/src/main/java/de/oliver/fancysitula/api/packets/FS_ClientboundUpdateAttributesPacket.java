package de.oliver.fancysitula.api.packets;

import java.util.List;

public abstract class FS_ClientboundUpdateAttributesPacket extends FS_ClientboundPacket {

    protected int entityId;
    protected List<AttributeSnapshot> attributes;

    public FS_ClientboundUpdateAttributesPacket(int entityId, List<AttributeSnapshot> attributes) {
        this.entityId = entityId;
        this.attributes = attributes;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public List<AttributeSnapshot> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeSnapshot> attributes) {
        this.attributes = attributes;
    }

    public record AttributeSnapshot(String attributeId, double baseValue) {}
}
