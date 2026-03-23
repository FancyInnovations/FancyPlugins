package com.fancyinnovations.fancyworlds.worlds.storage.fake;

import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.api.worlds.WorldStorage;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FakeWorldStorage implements WorldStorage {

    private final Map<UUID, FWorld> worlds;

    public FakeWorldStorage() {
        this.worlds = new ConcurrentHashMap<>();
    }

    @Override
    public void storeWorld(FWorld world) {
        this.worlds.put(world.getID(), world);
    }

    @Override
    public Collection<FWorld> getAllWorlds() {
        return this.worlds.values();
    }

    @Override
    public void deleteWorld(String id) {
        this.worlds.remove(UUID.fromString(id));
    }
}
