package com.fancyinnovations.fancyworlds.worlds.service;

import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.api.worlds.WorldService;
import com.fancyinnovations.fancyworlds.api.worlds.WorldStorage;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorldServiceImpl implements WorldService {

    private final WorldStorage storage;
    private final Map<String, FWorld> cacheByID;
    private final Map<String, FWorld> cacheByName;

    public WorldServiceImpl(WorldStorage storage) {
        this.storage = storage;
        this.cacheByID = new ConcurrentHashMap<>();
        this.cacheByName = new ConcurrentHashMap<>();

        Collection<FWorld> allWorlds = storage.getAllWorlds();
        for (FWorld w : allWorlds) {
            this.cacheByID.put(w.getID().toString(), w);
            this.cacheByName.put(w.getName(), w);
        }
    }

    @Override
    public void registerWorld(FWorld world) {
        this.storage.storeWorld(world);

        this.cacheByID.put(world.getID().toString(), world);
        this.cacheByName.put(world.getName(), world);
    }

    @Override
    public void unregisterWorld(FWorld world) {
        this.storage.deleteWorld(world.getID().toString());

        this.cacheByID.remove(world.getID().toString());
        this.cacheByName.remove(world.getName());
    }

    @Override
    public FWorld getWorldByID(String id) {
        return this.cacheByID.get(id);
    }

    @Override
    public FWorld getWorldByName(String name) {
        return this.cacheByName.get(name);
    }

    @Override
    public Collection<FWorld> getAllWorlds() {
        return this.cacheByID.values();
    }
}
