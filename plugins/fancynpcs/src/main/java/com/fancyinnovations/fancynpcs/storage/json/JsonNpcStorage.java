package com.fancyinnovations.fancynpcs.storage.json;

import com.fancyinnovations.fancynpcs.FancyNpcs;
import com.fancyinnovations.fancynpcs.api.Npc;
import com.fancyinnovations.fancynpcs.api.NpcData;
import com.fancyinnovations.fancynpcs.api.NpcStorage;
import com.fancyinnovations.fancynpcs.storage.json.model.JsonNpcModel;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.jdb.JDB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * JSON multi-file storage implementation for NPCs.
 * Stores each NPC in its own JSON file within the plugins/FancyNpcs/data/npcs/ directory.
 * Supports recursive loading from subdirectories.
 */
public class JsonNpcStorage implements NpcStorage {

    private static final String DATA_DIR_PATH = "plugins/FancyNpcs/data/npcs";
    private static final File DATA_DIR = new File(DATA_DIR_PATH);
    private final JDB jdb;
    private final Function<NpcData, Npc> npcAdapter;

    public JsonNpcStorage(Function<NpcData, Npc> npcAdapter) {
        this.jdb = new JDB(DATA_DIR_PATH);
        this.npcAdapter = npcAdapter;

        // Ensure data directory exists
        if (!DATA_DIR.exists()) {
            DATA_DIR.mkdirs();
        }
    }

    @Override
    public void saveBatch(@NotNull Collection<Npc> npcs, boolean override) {
        for (Npc npc : npcs) {
            if (!npc.isSaveToFile()) {
                continue;
            }

            boolean shouldSave = override || npc.isDirty();
            if (!shouldSave) {
                continue;
            }

            save(npc);
        }

        FancyNpcs.getInstance().getFancyLogger().debug("Saved " + npcs.size() + " NPCs to file (override=" + override + ")");
    }

    @Override
    public void save(@NotNull Npc npc) {
        if (!npc.isSaveToFile()) {
            return;
        }

        NpcData data = npc.getData();

        // Get file path for this NPC
        String filePath = data.getFilePath();
        if (filePath == null || filePath.isEmpty()) {
            FancyNpcs.getInstance().getFancyLogger().error("NPC " + data.getName() + " has no file path set");
            return;
        }

        JsonNpcModel jsonModel = JsonNpcAdapter.toJson(data);

        try {
            jdb.set(filePath, jsonModel);
            npc.setDirty(false);
            FancyNpcs.getInstance().getFancyLogger().debug("Saved NPC " + data.getName() + " to file " + filePath);
        } catch (IOException e) {
            FancyNpcs.getInstance().getFancyLogger().error("Failed to save NPC " + data.getName(), ThrowableProperty.of(e));
        }
    }

    @Override
    public void delete(@NotNull Npc npc) {
        NpcData data = npc.getData();
        String filePath = data.getFilePath();
        if (filePath == null || filePath.isEmpty()) {
            FancyNpcs.getInstance().getFancyLogger().error("NPC " + data.getName() + " has no file path set");
            return;
        }

        jdb.delete(filePath);
        FancyNpcs.getInstance().getFancyLogger().debug("Deleted NPC file " + filePath);
    }

    @Override
    public @NotNull Collection<Npc> loadAll() {
        return loadAll(null);
    }

    @Override
    public @NotNull Collection<Npc> loadAll(@Nullable String world) {
        List<Npc> npcs = new ArrayList<>();

        if (world != null) {
            // Load from specific world directory
            npcs.addAll(loadAllFromPath(world, world));
        } else {
            // Load from all directories recursively
            npcs.addAll(loadAllFromPath("", null));
        }

        FancyNpcs.getInstance().getFancyLogger().debug("Loaded " + npcs.size() + " NPCs from file" + (world != null ? " (world=" + world + ")" : ""));
        return npcs;
    }

    /**
     * Recursively loads all NPCs from the specified path.
     *
     * @param path        The relative path within the data directory
     * @param worldFilter Optional world filter to only load NPCs from a specific world
     * @return Collection of loaded NPCs
     */
    private Collection<Npc> loadAllFromPath(String path, @Nullable String worldFilter) {
        List<Npc> npcs = new ArrayList<>();

        File dir = path.isEmpty() ? DATA_DIR : new File(DATA_DIR, path);
        if (!dir.isDirectory()) {
            return npcs;
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return npcs;
        }

        for (File file : files) {
            String fileName = file.getName();

            if (file.isDirectory()) {
                // Recursively load from subdirectories
                String subPath = path.isEmpty() ? fileName : path + "/" + fileName;
                npcs.addAll(loadAllFromPath(subPath, worldFilter));
                continue;
            }

            // Skip hidden files
            if (fileName.startsWith(".") || fileName.startsWith("_")) {
                continue;
            }

            // Check if the file is a JSON file
            if (fileName.endsWith(".json")) {
                String filePath = path.isEmpty() ? fileName.substring(0, fileName.length() - 5) : path + "/" + fileName.substring(0, fileName.length() - 5);
                Npc npc = loadFile(filePath, worldFilter);
                if (npc != null) {
                    npcs.add(npc);
                }
            } else {
                FancyNpcs.getInstance().getFancyLogger().warn("File " + fileName + " is not a valid NPC file");
            }
        }

        return npcs;
    }

    /**
     * Loads an NPC from a specific file.
     *
     * @param path        The file path (without .json extension)
     * @param worldFilter Optional world filter to only load NPCs from a specific world
     * @return The loaded NPC, or null if not found or filtered out
     */
    private Npc loadFile(String path, @Nullable String worldFilter) {
        try {
            JsonNpcModel model = jdb.get(path, JsonNpcModel.class);
            if (model == null) {
                FancyNpcs.getInstance().getFancyLogger().debug("File " + path + " is empty or does not exist");
                return null;
            }

            // Apply world filter if specified
            if (worldFilter != null && !worldFilter.equals(model.location().world())) {
                return null;
            }

            NpcData data = JsonNpcAdapter.fromJson(model);
            if (data == null) {
                return null;
            }

            data.setFilePath(path);
            Npc npc = npcAdapter.apply(data);
            npc.create();
            npc.setDirty(false);
            return npc;
        } catch (IOException e) {
            FancyNpcs.getInstance().getFancyLogger().error("Failed to load NPC from " + path, ThrowableProperty.of(e));
            return null;
        }
    }
}
