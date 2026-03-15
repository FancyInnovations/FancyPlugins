package de.oliver.fancyvisuals.tablist.data;

import com.google.gson.annotations.SerializedName;
import de.oliver.fancysitula.api.utils.FS_GameProfile;
import org.jetbrains.annotations.Nullable;

public record TablistSkin(
        @SerializedName("texture_value")
        @Nullable String textureValue,
        @SerializedName("texture_signature")
        @Nullable String textureSignature,
        @SerializedName("skin_texture_asset")
        @Nullable String skinTextureAsset,
        @SerializedName("cape_texture_asset")
        @Nullable String capeTextureAsset,
        @SerializedName("elytra_texture_asset")
        @Nullable String elytraTextureAsset,
        @SerializedName("model")
        @Nullable Model model
) {

    public void applyTo(FS_GameProfile profile) {
        if (textureValue != null && textureSignature != null) {
            profile.getProperties().put("textures", new FS_GameProfile.Property("textures", textureValue, textureSignature));
        }

        if (skinTextureAsset != null) {
            profile.setSkinTextureAsset(skinTextureAsset);
        }
        if (capeTextureAsset != null) {
            profile.setCapeTextureAsset(capeTextureAsset);
        }
        if (elytraTextureAsset != null) {
            profile.setElytraTextureAsset(elytraTextureAsset);
        }

        if (model != null) {
            profile.setModelType(model == Model.SLIM ? "SLIM" : "DEFAULT");
        }
    }

    public enum Model {
        @SerializedName("default")
        DEFAULT,
        @SerializedName("slim")
        SLIM
    }
}
