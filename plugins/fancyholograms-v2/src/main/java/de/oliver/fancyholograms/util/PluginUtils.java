package de.oliver.fancyholograms.util;

import com.viaversion.viaversion.api.Via;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PluginUtils {

    /**
     * Protocol version for 1.19.4 - the minimum version that supports multi-line text displays.
     */
    private static final int MINIMUM_PROTOCOL_VERSION = 762;

    private static final Map<UUID, Integer> protocolVersionCache = new ConcurrentHashMap<>();

    public static boolean isFancyNpcsEnabled() {
        return Bukkit.getPluginManager().getPlugin("FancyNpcs") != null;
    }

    public static boolean isFloodgateEnabled() {
        return Bukkit.getPluginManager().getPlugin("floodgate") != null;
    }

    public static boolean isViaVersionEnabled() {
        return Bukkit.getPluginManager().getPlugin("ViaVersion") != null;
    }

    /**
     * Gets the protocol version of a player, using a cache to avoid repeated API calls.
     * If ViaVersion is not installed, returns the minimum protocol version (assumes modern client).
     *
     * @param player The player to get the protocol version for
     * @return The player's protocol version
     */
    public static int getPlayerProtocolVersion(Player player) {
        if (!isViaVersionEnabled()) {
            return MINIMUM_PROTOCOL_VERSION;
        }
        return protocolVersionCache.computeIfAbsent(
                player.getUniqueId(),
                uuid -> Via.getAPI().getPlayerProtocolVersion(uuid).getVersion()
        );
    }

    /**
     * Checks if a player is using a legacy client (pre-1.19.4, protocol < 762).
     * Legacy clients cannot properly display multi-line text displays.
     *
     * @param player The player to check
     * @return true if the player is using a legacy client
     */
    public static boolean isLegacyClient(Player player) {
        return getPlayerProtocolVersion(player) < MINIMUM_PROTOCOL_VERSION;
    }

    /**
     * Clears the cached protocol version for a player.
     * Should be called when a player disconnects.
     *
     * @param playerId The UUID of the player to clear the cache for
     */
    public static void clearProtocolCache(UUID playerId) {
        protocolVersionCache.remove(playerId);
    }
}
