package com.fancyinnovations.fancynpcs.skins.uuidcache;

import java.util.UUID;

public interface UUIDCache {

    long CACHE_TIME = 1000 * 60 * 60 * 24 * 30L; // 1 month

    UUID getUUID(String username);

    void cacheUUID(String username, UUID uuid);

    void clearCache();

}
