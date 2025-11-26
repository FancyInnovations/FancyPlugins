package de.oliver.fancysitula.versions.v1_21_11.utils;

import com.mojang.authlib.GameProfile;
import de.oliver.fancysitula.api.utils.FS_GameProfile;

import java.util.Map;

public class GameProfileImpl {

    public static GameProfile asVanilla(FS_GameProfile gameProfile) {
        GameProfile gf = new GameProfile(gameProfile.getUUID(), gameProfile.getName());

        for (Map.Entry<String, FS_GameProfile.Property> entry : gameProfile.getProperties().entrySet()) {
            FS_GameProfile.Property property = entry.getValue();

            gf.properties().put(entry.getKey(), new com.mojang.authlib.properties.Property(property.name(), property.value(), property.signature()));
        }

        return gf;
    }

    public static FS_GameProfile fromVanilla(GameProfile gameProfile) {
        FS_GameProfile fsGameProfile = new FS_GameProfile(gameProfile.id(), gameProfile.name());

        for (Map.Entry<String, com.mojang.authlib.properties.Property> entry : gameProfile.properties().entries()) {
            com.mojang.authlib.properties.Property property = entry.getValue();

            fsGameProfile.getProperties().put(entry.getKey(), new FS_GameProfile.Property(property.name(), property.value(), property.signature()));
        }

        return fsGameProfile;
    }
}
