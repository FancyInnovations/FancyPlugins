package de.oliver.fancysitula.versions.v1_21_9.utils;

import com.google.common.collect.ImmutableMultimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import de.oliver.fancysitula.api.utils.FS_GameProfile;

import java.util.Map;

public class GameProfileImpl {

    public static GameProfile asVanilla(FS_GameProfile gameProfile) {
        // Build properties map first since GameProfile.properties() is immutable
        ImmutableMultimap.Builder<String, Property> builder = ImmutableMultimap.builder();
        for (Map.Entry<String, FS_GameProfile.Property> entry : gameProfile.getProperties().entrySet()) {
            FS_GameProfile.Property property = entry.getValue();
            builder.put(entry.getKey(), new Property(property.name(), property.value(), property.signature()));
        }
        PropertyMap propertyMap = new PropertyMap(builder.build());

        return new GameProfile(gameProfile.getUUID(), gameProfile.getName(), propertyMap);
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
