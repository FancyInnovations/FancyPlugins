package de.oliver.fancynpcs.skins.uuidcache;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UUIDMemoryCache implements UUIDCache {

    private final Map<String, UUIDCacheData> cache;

    public UUIDMemoryCache() {
        this.cache = new ConcurrentHashMap<>();
    }

    @Override
    public UUID getUUID(String username) {
        if (!cache.containsKey(username)) {
            return null;
        }

        UUIDCacheData data = cache.get(username);
        if (data.isExpired()) {
            cache.remove(username);
            return null;
        }

        return data.uuid();
    }

    @Override
    public void cacheUUID(String username, UUID uuid) {
        UUIDCacheData data = new UUIDCacheData(username, uuid, System.currentTimeMillis(), CACHE_TIME);
        cache.put(username, data);
    }

    @Override
    public void clearCache() {
        cache.clear();
    }
}
