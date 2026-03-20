package com.fancyinnovations.fancyworlds.worlds.storage.json;

import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.api.worlds.WorldStorage;
import com.fancyinnovations.fancyworlds.main.FancyWorldsPlugin;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.jdb.JDB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JsonWorldStorage implements WorldStorage {

    private final JDB jdb;

    public JsonWorldStorage() {
        this.jdb = new JDB("plugins/FancyWorlds/data/worlds");
    }

    @Override
    public void storeWorld(FWorld world) {
        try {
            jdb.set(world.getID().toString(), JsonFWorld.fromFWorld(world));
        } catch (IOException e) {
            FancyWorldsPlugin.get().getFancyLogger().error("Failed to save world " + world.getID(), ThrowableProperty.of(e));
        }
    }

    @Override
    public Collection<FWorld> getAllWorlds() {
        List<FWorld> worlds = new ArrayList<>();

        try {
            List<JsonFWorld> allWorlds = jdb.getAll("", JsonFWorld.class);
            for (JsonFWorld jsonWorld : allWorlds) {
                worlds.add(jsonWorld.toFWorld());
            }
        } catch (IOException e) {
            FancyWorldsPlugin.get().getFancyLogger().error("Failed to load all worlds", ThrowableProperty.of(e));
        }

        return worlds;
    }

    @Override
    public void deleteWorld(String id) {
        jdb.delete(id);
    }
}
