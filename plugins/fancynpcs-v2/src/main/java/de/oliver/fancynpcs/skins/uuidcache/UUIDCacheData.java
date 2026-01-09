package de.oliver.fancynpcs.skins.uuidcache;

import java.util.UUID;

public record UUIDCacheData(
        String username,
        UUID uuid,
        long lastUpdated,
        long timeToLive
) {
    public boolean isExpired() {
        return timeToLive > 0 && System.currentTimeMillis() - lastUpdated > timeToLive;
    }
}
