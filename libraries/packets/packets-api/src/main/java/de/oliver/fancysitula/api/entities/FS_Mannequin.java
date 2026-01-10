package de.oliver.fancysitula.api.entities;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;
import de.oliver.fancysitula.api.utils.FS_EquipmentSlot;
import de.oliver.fancysitula.api.utils.FS_GameProfile;
import de.oliver.fancysitula.api.utils.entityData.FS_AvatarData;
import de.oliver.fancysitula.api.utils.entityData.FS_EntityData;
import de.oliver.fancysitula.api.utils.entityData.FS_MannequinData;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mannequin entity for FancySitula (1.21.11+)
 * Mannequin is a player-like entity that doesn't require tab list entries,
 * making it ideal for NPCs with better performance than fake players.
 */
public class FS_Mannequin extends FS_Entity {

    protected Map<FS_EquipmentSlot, ItemStack> equipment;

    // Avatar data (inherited from Avatar base class)
    protected FS_ClientboundSetEntityDataPacket.EntityData skinCustomizationData =
            new FS_ClientboundSetEntityDataPacket.EntityData(FS_AvatarData.DATA_PLAYER_MODE_CUSTOMISATION, FS_AvatarData.SKIN_ALL);

    protected FS_ClientboundSetEntityDataPacket.EntityData mainHandData =
            new FS_ClientboundSetEntityDataPacket.EntityData(FS_AvatarData.DATA_PLAYER_MAIN_HAND, null);

    // Mannequin-specific data
    protected FS_ClientboundSetEntityDataPacket.EntityData profileData =
            new FS_ClientboundSetEntityDataPacket.EntityData(FS_MannequinData.DATA_PROFILE, null);

    protected FS_ClientboundSetEntityDataPacket.EntityData immovableData =
            new FS_ClientboundSetEntityDataPacket.EntityData(FS_MannequinData.DATA_IMMOVABLE, true);

    protected FS_ClientboundSetEntityDataPacket.EntityData descriptionData =
            new FS_ClientboundSetEntityDataPacket.EntityData(FS_MannequinData.DATA_DESCRIPTION, null);

    // Pose data (from Entity base class, but Mannequin only supports specific poses)
    protected FS_ClientboundSetEntityDataPacket.EntityData poseData =
            new FS_ClientboundSetEntityDataPacket.EntityData(FS_EntityData.POSE, "STANDING");

    // Valid Mannequin poses
    public static final String POSE_STANDING = "STANDING";
    public static final String POSE_CROUCHING = "CROUCHING";
    public static final String POSE_SWIMMING = "SWIMMING";
    public static final String POSE_FLYING = "FALL_FLYING";
    public static final String POSE_SLEEPING = "SLEEPING";

    public FS_Mannequin() {
        super(EntityType.MANNEQUIN);
        this.equipment = new ConcurrentHashMap<>();
    }

    // Equipment methods

    public Map<FS_EquipmentSlot, ItemStack> getEquipment() {
        return Collections.unmodifiableMap(equipment);
    }

    public ItemStack getEquipment(FS_EquipmentSlot slot) {
        return equipment.get(slot);
    }

    public void setEquipment(Map<FS_EquipmentSlot, ItemStack> equipment) {
        if (equipment == null) {
            this.equipment.clear();
            return;
        }
        this.equipment.clear();
        this.equipment.putAll(equipment);
    }

    public void setEquipment(FS_EquipmentSlot slot, ItemStack item) {
        if (slot == null) {
            return;
        }
        if (item == null) {
            this.equipment.remove(slot);
        } else {
            this.equipment.put(slot, item);
        }
    }

    // Avatar data methods

    public byte getSkinCustomization() {
        Object value = skinCustomizationData.getValue();
        return value != null ? (byte) value : FS_AvatarData.SKIN_ALL;
    }

    public void setSkinCustomization(byte skinCustomization) {
        this.skinCustomizationData.setValue(skinCustomization);
    }

    /**
     * @return "LEFT" or "RIGHT"
     */
    public String getMainHand() {
        Object value = mainHandData.getValue();
        return value != null ? (String) value : "RIGHT";
    }

    /**
     * @param mainHand "LEFT" or "RIGHT"
     */
    public void setMainHand(String mainHand) {
        this.mainHandData.setValue(mainHand);
    }

    // Mannequin-specific methods

    public FS_GameProfile getProfile() {
        return (FS_GameProfile) profileData.getValue();
    }

    public void setProfile(FS_GameProfile profile) {
        this.profileData.setValue(profile);
    }

    public boolean isImmovable() {
        Object value = immovableData.getValue();
        return value != null ? (boolean) value : true;
    }

    public void setImmovable(boolean immovable) {
        this.immovableData.setValue(immovable);
    }

    public Optional<Component> getDescription() {
        @SuppressWarnings("unchecked")
        Optional<Component> value = (Optional<Component>) descriptionData.getValue();
        return value != null ? value : Optional.empty();
    }

    public void setDescription(Optional<Component> description) {
        this.descriptionData.setValue(description);
    }

    public void setDescription(Component description) {
        this.descriptionData.setValue(description != null ? Optional.of(description) : Optional.empty());
    }

    // Pose methods

    /**
     * Get the current pose.
     * @return One of POSE_STANDING, POSE_CROUCHING, POSE_SWIMMING, POSE_FLYING, POSE_SLEEPING
     */
    public String getPose() {
        Object value = poseData.getValue();
        return value != null ? (String) value : POSE_STANDING;
    }

    /**
     * Set the mannequin's pose.
     * @param pose One of POSE_STANDING, POSE_CROUCHING, POSE_SWIMMING, POSE_FLYING, POSE_SLEEPING
     *             or the raw Pose enum name (STANDING, CROUCHING, SWIMMING, FALL_FLYING, SLEEPING)
     */
    public void setPose(String pose) {
        if (pose == null) {
            this.poseData.setValue(POSE_STANDING);
            return;
        }
        // Normalize pose name
        String normalizedPose = switch (pose.toUpperCase()) {
            case "STANDING" -> POSE_STANDING;
            case "CROUCHING", "SNEAKING", "CROUCH", "SNEAK" -> POSE_CROUCHING;
            case "SWIMMING", "SWIM" -> POSE_SWIMMING;
            case "FLYING", "FALL_FLYING", "ELYTRA", "FLY" -> POSE_FLYING;
            case "SLEEPING", "SLEEP" -> POSE_SLEEPING;
            default -> POSE_STANDING;
        };
        this.poseData.setValue(normalizedPose);
    }

    @Override
    public List<FS_ClientboundSetEntityDataPacket.EntityData> getEntityData() {
        List<FS_ClientboundSetEntityDataPacket.EntityData> entityData = super.getEntityData();
        // Avatar data
        entityData.add(this.skinCustomizationData);
        if (this.mainHandData.getValue() != null) {
            entityData.add(this.mainHandData);
        }
        // Mannequin data
        if (this.profileData.getValue() != null) {
            entityData.add(this.profileData);
        }
        entityData.add(this.immovableData);
        if (this.descriptionData.getValue() != null) {
            entityData.add(this.descriptionData);
        }
        // Pose data
        entityData.add(this.poseData);
        return entityData;
    }
}
