package de.oliver.fancynpcs.skins.uuidcache;

import com.google.gson.Gson;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.jdb.JDB;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UUIDFileCache implements UUIDCache {

    private final static File usercacheFile = new File("usercache.json");
    private final static Gson gson = new Gson();
    private final JDB storage;
    private final Map<String, UUIDCacheData> cache;
    private final Map<String, UUID> minecraftUsercache;

    public UUIDFileCache() {
        this.storage = new JDB("plugins/FancyNpcs/.data");
        this.cache = new ConcurrentHashMap<>();
        this.minecraftUsercache = new ConcurrentHashMap<>();

        loadMinecraftUsercache();
        load();
    }

    private void load() {
        UUIDCacheData[] uuids;
        try {
            uuids = this.storage.get("uuids", UUIDCacheData[].class);
        } catch (IOException e) {
            FancyNpcs.getInstance().getFancyLogger().error("Failed to load UUID cache", ThrowableProperty.of(e));
            return;
        }

        if (uuids == null) {
            return;
        }

        cache.clear();

        for (UUIDCacheData data : uuids) {
            if (!data.isExpired()) {
                cache.put(data.username(), data);
            }
        }

        FancyNpcs.getInstance().getFancyLogger().debug("Loaded " + cache.size() + " UUIDs from file cache");
    }

    private void loadMinecraftUsercache() {
        if (!FancyNpcs.USE_MINECRAFT_USERCACHE_FEATURE_FLAG.isEnabled()) {
            return;
        }

        if (!usercacheFile.exists()) {
            FancyNpcs.getInstance().getFancyLogger().debug("Minecraft usercache file does not exist, skipping load.");
            return;
        }

        MinecraftUsercacheData[] data;
        try {
            data = gson.fromJson(new FileReader(usercacheFile), MinecraftUsercacheData[].class);
        } catch (FileNotFoundException e) {
            FancyNpcs.getInstance().getFancyLogger().error("Failed to load Minecraft usercache file");
            return;
        }

        if (data == null) {
            return;
        }

        for (MinecraftUsercacheData d : data) {
            minecraftUsercache.put(d.name(), UUID.fromString(d.uuid()));
        }

        FancyNpcs.getInstance().getFancyLogger().debug("Loaded " + minecraftUsercache.size() + " UUIDs from Minecraft usercache");
    }

    private void save() {
        UUIDCacheData[] uuids = cache.values().toArray(new UUIDCacheData[0]);
        try {
            this.storage.set("uuids", uuids);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save UUID cache", e);
        }
    }

    @Override
    public UUID getUUID(String username) {
        if (minecraftUsercache.containsKey(username)) {
            FancyNpcs.getInstance().getFancyLogger().debug("Found UUID for " + username + ": " + minecraftUsercache.get(username) + " in Minecraft usercache");
            return minecraftUsercache.get(username);
        }

        if (cache.isEmpty()) {
            load(); // Load from storage if not present in cache
        }

        if (!cache.containsKey(username)) {
            return null; // If still not present after loading, return null
        }

        UUIDCacheData data = cache.get(username);
        if (data.isExpired()) {
            cache.remove(username);
            save(); // Save changes after removing expired entry
            return null; // If expired, remove from cache and return null
        }

        FancyNpcs.getInstance().getFancyLogger().debug("Found UUID for " + username + ": " + data.uuid() + " in file cache");

        return data.uuid();
    }

    @Override
    public void cacheUUID(String username, UUID uuid) {
        UUIDCacheData data = new UUIDCacheData(username, uuid, System.currentTimeMillis(), CACHE_TIME);
        cache.put(username, data);
        save(); // Save changes after adding new entry
        FancyNpcs.getInstance().getFancyLogger().debug("Cached UUID for " + username + ": " + uuid);
    }

    @Override
    public void clearCache() {
        cache.clear();
        storage.delete("uuids");
    }
}
