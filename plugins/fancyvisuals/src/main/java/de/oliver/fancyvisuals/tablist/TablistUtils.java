package de.oliver.fancyvisuals.tablist;

import de.oliver.fancysitula.api.utils.FS_GameProfile;
import de.oliver.fancysitula.api.utils.FS_GameType;
import de.oliver.fancyvisuals.tablist.data.CustomTablistEntry;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class TablistUtils {

    private TablistUtils() {
    }

    public static FS_GameProfile toProfile(Player player) {
        return FS_GameProfile.fromBukkit(player.getPlayerProfile());
    }

    public static FS_GameType toGameType(GameMode gameMode) {
        if (gameMode == null) {
            return FS_GameType.SURVIVAL;
        }

        return switch (gameMode) {
            case CREATIVE -> FS_GameType.CREATIVE;
            case ADVENTURE -> FS_GameType.ADVENTURE;
            case SPECTATOR -> FS_GameType.SPECTATOR;
            case SURVIVAL -> FS_GameType.SURVIVAL;
        };
    }

    public static FS_GameType toGameType(String name) {
        if (name == null) {
            return FS_GameType.SURVIVAL;
        }

        try {
            return FS_GameType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return FS_GameType.SURVIVAL;
        }
    }

    public static UUID buildEntryUuid(Player viewer, CustomTablistEntry entry) {
        String key = viewer.getUniqueId() + ":" + entry.id();
        return UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8));
    }

    public static String buildProfileName(CustomTablistEntry entry) {
        String base = entry.profileName();
        String fallback = "fv" + shortHash(entry.id());

        if (base == null || base.isBlank()) {
            base = fallback;
        }

        String sanitized = base.replaceAll("[^A-Za-z0-9_]", "");
        if (sanitized.isEmpty()) {
            sanitized = fallback;
        }

        if (sanitized.length() > 16) {
            sanitized = sanitized.substring(0, 16);
        }

        return sanitized;
    }

    private static String shortHash(String value) {
        String hex = Integer.toHexString(value.hashCode());
        if (hex.length() > 10) {
            return hex.substring(0, 10);
        }
        return hex;
    }
}
