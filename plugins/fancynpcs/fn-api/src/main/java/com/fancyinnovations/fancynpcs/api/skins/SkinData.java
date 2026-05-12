package com.fancyinnovations.fancynpcs.api.skins;

import org.lushplugins.chatcolorhandler.ChatColorHandler;

import java.util.Objects;

public class SkinData {

    private String identifier;
    private SkinVariant variant;

    private String textureValue;
    private String textureSignature;

    // Resource pack texture assets (for Mannequin 1.21.11+)
    // When isTexturePackSkin is true, identifier is used as the skin texture asset path
    // Format: "namespace:path" -> resolves to "assets/<namespace>/textures/<path>.png"
    private boolean texturePackSkin;
    private String capeTextureAsset;   // e.g., "myserver:capes/royal"
    private String elytraTextureAsset; // e.g., "myserver:elytra/dragon"

    public SkinData(String identifier, SkinVariant variant, String textureValue, String textureSignature) {
        this.identifier = identifier;
        this.variant = variant;
        this.textureValue = textureValue;
        this.textureSignature = textureSignature;
        this.texturePackSkin = false;
    }

    public SkinData(String identifier, SkinVariant variant) {
        this(identifier, variant, null, null);
    }

    /**
     * Creates a texture pack skin that uses resource pack textures.
     * For Mannequin entities on 1.21.11+.
     *
     * @param skinTextureAsset The skin texture asset path (e.g., "myserver:skins/npc_guard")
     *                         This will resolve to "assets/myserver/textures/skins/npc_guard.png" in resource pack
     * @param variant          The skin variant (SLIM for alex model, AUTO for steve model)
     * @return A new SkinData configured for texture pack skins
     */
    public static SkinData texturePackSkin(String skinTextureAsset, SkinVariant variant) {
        SkinData data = new SkinData(skinTextureAsset, variant, null, null);
        data.texturePackSkin = true;
        return data;
    }

    /**
     * Creates a texture pack skin with optional cape and elytra.
     *
     * @param skinTextureAsset   The skin texture asset path
     * @param capeTextureAsset   Optional cape texture asset path (null if none)
     * @param elytraTextureAsset Optional elytra texture asset path (null if none)
     * @param variant            The skin variant
     * @return A new SkinData configured for texture pack skins
     */
    public static SkinData texturePackSkin(String skinTextureAsset, String capeTextureAsset,
                                            String elytraTextureAsset, SkinVariant variant) {
        SkinData data = new SkinData(skinTextureAsset, variant, null, null);
        data.texturePackSkin = true;
        data.capeTextureAsset = capeTextureAsset;
        data.elytraTextureAsset = elytraTextureAsset;
        return data;
    }

    public boolean hasTexture() {
        return textureValue != null &&
                textureSignature != null &&
                !textureValue.isEmpty() &&
                !textureSignature.isEmpty();
    }

    /**
     * @return true if this skin uses resource pack textures (Mannequin 1.21.11+)
     */
    public boolean isTexturePackSkin() {
        return texturePackSkin;
    }

    public void setTexturePackSkin(boolean texturePackSkin) {
        this.texturePackSkin = texturePackSkin;
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

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getParsedIdentifier() {
        if (identifier.startsWith("%") && identifier.endsWith("%") || identifier.startsWith("{") && identifier.endsWith("}")) {
            return ChatColorHandler.translate(identifier);
        } else {
            return identifier;
        }
    }

    public SkinVariant getVariant() {
        return variant;
    }

    public void setVariant(SkinVariant variant) {
        this.variant = variant;
    }

    public String getTextureValue() {
        return textureValue;
    }

    public void setTextureValue(String textureValue) {
        this.textureValue = textureValue;
    }

    public String getTextureSignature() {
        return textureSignature;
    }

    public void setTextureSignature(String textureSignature) {
        this.textureSignature = textureSignature;
    }

    @Override
    public String toString() {
        return "SkinData{" +
                "identifier='" + identifier + '\'' +
                ", variant=" + variant +
                ", textureValue='" + textureValue + '\'' +
                ", textureSignature='" + textureSignature + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SkinData skinData = (SkinData) o;
        return texturePackSkin == skinData.texturePackSkin &&
                Objects.equals(identifier, skinData.identifier) &&
                variant == skinData.variant &&
                Objects.equals(textureValue, skinData.textureValue) &&
                Objects.equals(textureSignature, skinData.textureSignature) &&
                Objects.equals(capeTextureAsset, skinData.capeTextureAsset) &&
                Objects.equals(elytraTextureAsset, skinData.elytraTextureAsset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, variant, textureValue, textureSignature,
                texturePackSkin, capeTextureAsset, elytraTextureAsset);
    }

    public enum SkinVariant {
        AUTO,
        SLIM,
    }
}
