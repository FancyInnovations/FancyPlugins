package de.oliver.fancysitula.api.entities;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;
import de.oliver.fancysitula.api.utils.entityData.FS_DisplayData;
import org.bukkit.entity.EntityType;

import java.util.List;

public class FS_Display extends FS_Entity {

    protected final FS_ClientboundSetEntityDataPacket.EntityData transformationInterpolationStartDeltaTicksData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_DisplayData.TRANSFORMATION_INTERPOLATION_START_DELTA_TICKS, null);
    protected final FS_ClientboundSetEntityDataPacket.EntityData transformationInterpolationDurationData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_DisplayData.TRANSFORMATION_INTERPOLATION_DURATION, null);
    protected final FS_ClientboundSetEntityDataPacket.EntityData posRotInterpolationDurationData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_DisplayData.POS_ROT_INTERPOLATION_DURATION, null);
    protected final FS_ClientboundSetEntityDataPacket.EntityData translationData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_DisplayData.TRANSLATION, null);
    protected final FS_ClientboundSetEntityDataPacket.EntityData scaleData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_DisplayData.SCALE, null);
    protected final FS_ClientboundSetEntityDataPacket.EntityData leftRotationData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_DisplayData.LEFT_ROTATION, null);
    protected final FS_ClientboundSetEntityDataPacket.EntityData rightRotationData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_DisplayData.RIGHT_ROTATION, null);
    protected final FS_ClientboundSetEntityDataPacket.EntityData billboardRenderConstraintsData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_DisplayData.BILLBOARD_RENDER_CONSTRAINTS, null);
    protected final FS_ClientboundSetEntityDataPacket.EntityData brightnessOverrideData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_DisplayData.BRIGHTNESS_OVERRIDE, null);
    protected final FS_ClientboundSetEntityDataPacket.EntityData viewRangeData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_DisplayData.VIEW_RANGE, null);
    protected final FS_ClientboundSetEntityDataPacket.EntityData shadowRadiusData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_DisplayData.SHADOW_RADIUS, null);
    protected final FS_ClientboundSetEntityDataPacket.EntityData shadowStrengthData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_DisplayData.SHADOW_STRENGTH, null);
    protected final FS_ClientboundSetEntityDataPacket.EntityData widthData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_DisplayData.WIDTH, null);
    protected final FS_ClientboundSetEntityDataPacket.EntityData heightData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_DisplayData.HEIGHT, null);
    protected final FS_ClientboundSetEntityDataPacket.EntityData glowColorOverrideData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_DisplayData.GLOW_COLOR_OVERRIDE, null);

    public FS_Display(EntityType type) {
        super(type);
    }

    public int getTransformationInterpolationStartDeltaTicks() {
        return (int) this.transformationInterpolationStartDeltaTicksData.getValue();
    }

    public void setTransformationInterpolationStartDeltaTicks(int transformationInterpolationStartDeltaTicks) {
        this.transformationInterpolationStartDeltaTicksData.setValue(transformationInterpolationStartDeltaTicks);
    }

    public int getTransformationInterpolationDuration() {
        return (int) this.transformationInterpolationDurationData.getValue();
    }

    public void setTransformationInterpolationDuration(int transformationInterpolationDuration) {
        this.transformationInterpolationDurationData.setValue(transformationInterpolationDuration);
    }

    public int getPosRotInterpolationDuration() {
        return (int) this.posRotInterpolationDurationData.getValue();
    }

    public void setPosRotInterpolationDuration(int posRotInterpolationDuration) {
        this.posRotInterpolationDurationData.setValue(posRotInterpolationDuration);
    }

    public org.joml.Vector3f getTranslation() {
        return (org.joml.Vector3f) this.translationData.getValue();
    }

    public void setTranslation(org.joml.Vector3f translation) {
        this.translationData.setValue(translation);
    }

    public org.joml.Vector3f getScale() {
        return (org.joml.Vector3f) this.scaleData.getValue();
    }

    public void setScale(org.joml.Vector3f scale) {
        this.scaleData.setValue(scale);
    }

    public org.joml.Quaternionf getLeftRotation() {
        return (org.joml.Quaternionf) this.leftRotationData.getValue();
    }

    public void setLeftRotation(org.joml.Quaternionf leftRotation) {
        this.leftRotationData.setValue(leftRotation);
    }

    public org.joml.Quaternionf getRightRotation() {
        return (org.joml.Quaternionf) this.rightRotationData.getValue();
    }

    public void setRightRotation(org.joml.Quaternionf rightRotation) {
        this.rightRotationData.setValue(rightRotation);
    }

    public byte getBillboardRenderConstraints() {
        return (byte) this.billboardRenderConstraintsData.getValue();
    }

    public void setBillboardRenderConstraints(byte billboardRenderConstraints) {
        this.billboardRenderConstraintsData.setValue(billboardRenderConstraints);
    }

    public Billboard getBillboard() {
        return Billboard.getById(getBillboardRenderConstraints());
    }

    public void setBillboard(Billboard billboard) {
        this.billboardRenderConstraintsData.setValue(billboard.getId());
    }

    public int getBrightnessOverride() {
        return (int) this.brightnessOverrideData.getValue();
    }

    public void setBrightnessOverride(int brightnessOverride) {
        this.brightnessOverrideData.setValue(brightnessOverride);
    }

    public float getViewRange() {
        return (float) this.viewRangeData.getValue();
    }

    public void setViewRange(float viewRange) {
        this.viewRangeData.setValue(viewRange);
    }

    public float getShadowRadius() {
        return (float) this.shadowRadiusData.getValue();
    }

    public void setShadowRadius(float shadowRadius) {
        this.shadowRadiusData.setValue(shadowRadius);
    }

    public float getShadowStrength() {
        return (float) this.shadowStrengthData.getValue();
    }

    public void setShadowStrength(float shadowStrength) {
        this.shadowStrengthData.setValue(shadowStrength);
    }

    public int getWidth() {
        return (int) this.widthData.getValue();
    }

    public void setWidth(int width) {
        this.widthData.setValue(width);
    }

    public int getHeight() {
        return (int) this.heightData.getValue();
    }

    public void setHeight(int height) {
        this.heightData.setValue(height);
    }

    public int getGlowColorOverride() {
        return (int) this.glowColorOverrideData.getValue();
    }

    public void setGlowColorOverride(int glowColorOverride) {
        this.glowColorOverrideData.setValue(glowColorOverride);
    }

    @Override
    public List<FS_ClientboundSetEntityDataPacket.EntityData> getEntityData() {
        List<FS_ClientboundSetEntityDataPacket.EntityData> entityData = super.getEntityData();

        entityData.add(this.transformationInterpolationStartDeltaTicksData);
        entityData.add(this.transformationInterpolationDurationData);
        entityData.add(this.posRotInterpolationDurationData);
        entityData.add(this.translationData);
        entityData.add(this.scaleData);
        entityData.add(this.leftRotationData);
        entityData.add(this.rightRotationData);
        entityData.add(this.billboardRenderConstraintsData);
        entityData.add(this.brightnessOverrideData);
        entityData.add(this.viewRangeData);
        entityData.add(this.shadowRadiusData);
        entityData.add(this.shadowStrengthData);
        entityData.add(this.widthData);
        entityData.add(this.heightData);
        entityData.add(this.glowColorOverrideData);
        return entityData;
    }

    public enum Billboard {
        FIXED((byte) 0, "fixed"),
        VERTICAL((byte) 1, "vertical"),
        HORIZONTAL((byte) 2, "horizontal"),
        CENTER((byte) 3, "center"),
        ;

        private final byte id;
        private final String name;

        Billboard(byte id, String name) {
            this.id = id;
            this.name = name;
        }

        public static Billboard getById(byte id) {
            for (Billboard value : values()) {
                if (value.getId() == id) {
                    return value;
                }
            }
            return null;
        }

        public byte getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

}
