package de.oliver.fancysitula.api.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FS_GameProfile {

    private UUID uuid;
    private String name;
    private Map<String, Property> properties;

    // Resource pack texture assets (for Mannequin 1.21.11+)
    // Format: "namespace:path" -> resolves to "assets/<namespace>/textures/<path>.png"
    private String skinTextureAsset;  // e.g., "myserver:skins/npc_guard"
    private String capeTextureAsset;  // e.g., "myserver:capes/royal"
    private String elytraTextureAsset; // e.g., "myserver:elytra/dragon"
    private String modelType; // "DEFAULT" (wide/steve) or "SLIM" (alex)

    public FS_GameProfile(UUID uuid, String name, Map<String, Property> properties) {
        this.uuid = uuid;
        this.name = name;
        this.properties = properties;
    }

    public FS_GameProfile(UUID uuid, String name) {
        this(uuid, name, new HashMap<>());
    }

    public static FS_GameProfile fromBukkit(PlayerProfile gameProfile) {
        FS_GameProfile fsGameProfile = new FS_GameProfile(gameProfile.getId(), gameProfile.getName());

        for (ProfileProperty property : gameProfile.getProperties()) {
            fsGameProfile.getProperties().put(property.getName(), new FS_GameProfile.Property(property.getName(), property.getValue(), property.getSignature()));
        }

        return fsGameProfile;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Property> properties) {
        this.properties = properties;
    }

    // Resource pack texture asset getters/setters

    public String getSkinTextureAsset() {
        return skinTextureAsset;
    }

    public void setSkinTextureAsset(String skinTextureAsset) {
        this.skinTextureAsset = skinTextureAsset;
    }

    public String getCapeTextureAsset() {
        return capeTextureAsset;
    }

    public void setCapeTextureAsset(String capeTextureAsset) {
        this.capeTextureAsset = capeTextureAsset;
    }

    public String getElytraTextureAsset() {
        return elytraTextureAsset;
    }

    public void setElytraTextureAsset(String elytraTextureAsset) {
        this.elytraTextureAsset = elytraTextureAsset;
    }

    /**
     * @return "DEFAULT" (wide/steve model) or "SLIM" (alex model)
     */
    public String getModelType() {
        return modelType;
    }

    /**
     * @param modelType "DEFAULT" (wide/steve model) or "SLIM" (alex model)
     */
    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    /**
     * @return true if this profile has any resource pack texture assets configured
     */
    public boolean hasTextureAssets() {
        return skinTextureAsset != null || capeTextureAsset != null || elytraTextureAsset != null;
    }

    public record Property(String name, String value, String signature) {
    }
}
