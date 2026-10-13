package de.oliver.fancysitula.api.entities;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;
import de.oliver.fancysitula.api.utils.entityData.FS_EntityData;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FS_Entity {

    private static int entityCount = Integer.MAX_VALUE / 2;

    protected int id = entityCount++;
    protected UUID uuid = UUID.randomUUID();
    protected EntityType type;

    protected double x = 0;
    protected double y = 0;
    protected double z = 0;

    protected float yaw = 0;
    protected float pitch = 0;
    protected float headYaw = 0;

    protected int velocityX = 0;
    protected int velocityY = 0;
    protected int velocityZ = 0;

    protected int data = 0;

    protected final FS_ClientboundSetEntityDataPacket.EntityData sharedFlagsData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_EntityData.SHARED_FLAGS, null);
    protected final FS_ClientboundSetEntityDataPacket.EntityData airSupplyData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_EntityData.AIR_SUPPLY, null);
    protected final FS_ClientboundSetEntityDataPacket.EntityData customNameData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_EntityData.CUSTOM_NAME, null);
    protected final FS_ClientboundSetEntityDataPacket.EntityData customNameVisibleData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_EntityData.CUSTOM_NAME_VISIBLE, null);
    protected final FS_ClientboundSetEntityDataPacket.EntityData silentData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_EntityData.SILENT, null);
    protected final FS_ClientboundSetEntityDataPacket.EntityData noGravityData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_EntityData.NO_GRAVITY, null);
    protected final FS_ClientboundSetEntityDataPacket.EntityData ticksFrozenData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_EntityData.TICKS_FROZEN, null);

    public FS_Entity(EntityType type) {
        this.type = type;
    }

    // Entity data

    public byte getSharedFlags() {
        return (byte) sharedFlagsData.getValue();
    }

    public void setSharedFlags(byte sharedFlags) {
        this.sharedFlagsData.setValue(sharedFlags);
    }

    public byte getAirSupply() {
        return (byte) airSupplyData.getValue();
    }

    public void setAirSupply(byte airSupply) {
        this.airSupplyData.setValue(airSupply);
    }

    public Optional<Component> getCustomName() {
        return (Optional<Component>) customNameData.getValue();
    }

    public void setCustomName(Optional<Component> customName) {
        this.customNameData.setValue(customName);
    }

    public boolean getCustomNameVisible() {
        return (boolean) customNameVisibleData.getValue();
    }

    public void setCustomNameVisible(boolean customNameVisible) {
        this.customNameVisibleData.setValue(customNameVisible);
    }

    public boolean getSilent() {
        return (boolean) silentData.getValue();
    }

    public void setSilent(boolean silent) {
        this.silentData.setValue(silent);
    }

    public boolean getNoGravity() {
        return (boolean) noGravityData.getValue();
    }

    public void setNoGravity(boolean noGravity) {
        this.noGravityData.setValue(noGravity);
    }

    public int getTicksFrozen() {
        return (int) ticksFrozenData.getValue();
    }

    public void setTicksFrozen(int ticksFrozen) {
        this.ticksFrozenData.setValue(ticksFrozen);
    }

    public List<FS_ClientboundSetEntityDataPacket.EntityData> getEntityData() {
        List<FS_ClientboundSetEntityDataPacket.EntityData> entityData = new ArrayList<>();

        entityData.add(this.sharedFlagsData);
        entityData.add(this.airSupplyData);
        entityData.add(this.customNameData);
        entityData.add(this.customNameVisibleData);
        entityData.add(this.silentData);
        entityData.add(this.noGravityData);
        entityData.add(this.ticksFrozenData);
        return entityData;
    }

    // Getter and Setter for all fields

    public void setLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setLocation(Location location) {
        setLocation(location.getX(), location.getY(), location.getZ());
        setRotation(location.getYaw(), location.getPitch());
    }

    public void setRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public void setVelocity(int velocityX, int velocityY, int velocityZ) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getHeadYaw() {
        return headYaw;
    }

    public void setHeadYaw(float headYaw) {
        this.headYaw = headYaw;
    }

    public int getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(int velocityX) {
        this.velocityX = velocityX;
    }

    public int getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(int velocityY) {
        this.velocityY = velocityY;
    }

    public int getVelocityZ() {
        return velocityZ;
    }

    public void setVelocityZ(int velocityZ) {
        this.velocityZ = velocityZ;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}
