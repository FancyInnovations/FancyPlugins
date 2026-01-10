package de.oliver.fancysitula.api.entities;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;
import de.oliver.fancysitula.api.utils.entityData.FS_TextDisplayData;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.EntityType;

import java.util.List;

public class FS_TextDisplay extends FS_Display {

    protected FS_ClientboundSetEntityDataPacket.EntityData textData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_TextDisplayData.TEXT, null);
    protected FS_ClientboundSetEntityDataPacket.EntityData lineWidthData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_TextDisplayData.LINE_WIDTH, null);
    protected FS_ClientboundSetEntityDataPacket.EntityData backgroundData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_TextDisplayData.BACKGROUND, null);
    protected FS_ClientboundSetEntityDataPacket.EntityData textOpacityData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_TextDisplayData.TEXT_OPACITY, null);
    protected FS_ClientboundSetEntityDataPacket.EntityData styleFlagsData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_TextDisplayData.STYLE_FLAGS, null);

    public FS_TextDisplay() {
        super(EntityType.TEXT_DISPLAY);
    }

    public Component getText() {
        return (Component) this.textData.getValue();
    }

    public void setText(Component text) {
        this.textData.setValue(text);
    }

    public int getLineWidth() {
        return (int) this.lineWidthData.getValue();
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidthData.setValue(lineWidth);
    }

    public int getBackground() {
        return (int) this.backgroundData.getValue();
    }

    public void setBackground(int background) {
        this.backgroundData.setValue(background);
    }

    public byte getTextOpacity() {
        return (byte) this.textOpacityData.getValue();
    }

    public void setTextOpacity(byte textOpacity) {
        this.textOpacityData.setValue(textOpacity);
    }

    public byte getStyleFlags() {
        Object value = this.styleFlagsData.getValue();
        return value != null ? (byte) value : 0;
    }

    public void setStyleFlags(byte styleFlags) {
        this.styleFlagsData.setValue(styleFlags);
    }

    public void setStyleFlag(byte flag, boolean value) {
        byte styleFlags = getStyleFlags();
        if (value) {
            this.styleFlagsData.setValue((byte) (styleFlags | flag));
        } else {
            this.styleFlagsData.setValue((byte) (styleFlags & ~flag));
        }
    }

    public boolean hasStyleFlag(byte flag) {
        return (getStyleFlags() & flag) == flag;
    }

    public void setShadow(boolean shadow) {
        setStyleFlag((byte) 1, shadow);
    }

    public boolean hasShadow() {
        return hasStyleFlag((byte) 1);
    }

    public boolean isSeeThrough() {
        return hasStyleFlag((byte) 2);
    }

    public void setSeeThrough(boolean seeThrough) {
        setStyleFlag((byte) 2, seeThrough);
    }

    public void setUseDefaultBackground(boolean defaultBackground) {
        setStyleFlag((byte) 4, defaultBackground);
    }

    public boolean isUsingDefaultBackground() {
        return hasStyleFlag((byte) 4);
    }

    public boolean isAlignLeft() {
        return hasStyleFlag((byte) 8);
    }

    public void setAlignLeft(boolean alignLeft) {
        setStyleFlag((byte) 8, alignLeft);
    }

    public void setAlignRight(boolean alignRight) {
        setStyleFlag((byte) 16, alignRight);
    }

    @Override
    public List<FS_ClientboundSetEntityDataPacket.EntityData> getEntityData() {
        List<FS_ClientboundSetEntityDataPacket.EntityData> entityData = super.getEntityData();

        entityData.add(this.textData);
        entityData.add(this.lineWidthData);
        entityData.add(this.backgroundData);
        entityData.add(this.textOpacityData);
        entityData.add(this.styleFlagsData);
        return entityData;
    }
}

