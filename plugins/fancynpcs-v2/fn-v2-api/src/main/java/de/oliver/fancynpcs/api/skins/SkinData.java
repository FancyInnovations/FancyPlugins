package de.oliver.fancynpcs.api.skins;

import org.lushplugins.chatcolorhandler.ChatColorHandler;

import java.util.Objects;

public class SkinData {

    private String identifier;
    private SkinVariant variant;

    private String textureValue;
    private String textureSignature;

    public SkinData(String identifier, SkinVariant variant, String textureValue, String textureSignature) {
        this.identifier = identifier;
        this.variant = variant;
        this.textureValue = textureValue;
        this.textureSignature = textureSignature;
    }

    public SkinData(String identifier, SkinVariant variant) {
        this(identifier, variant, null, null);
    }

    public boolean hasTexture() {
        return textureValue != null &&
                textureSignature != null &&
                !textureValue.isEmpty() &&
                !textureSignature.isEmpty();
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
        return Objects.equals(identifier, skinData.identifier) &&
                variant == skinData.variant &&
                Objects.equals(textureValue, skinData.textureValue) &&
                Objects.equals(textureSignature, skinData.textureSignature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, variant, textureValue, textureSignature);
    }

    public enum SkinVariant {
        AUTO,
        SLIM,
    }
}
