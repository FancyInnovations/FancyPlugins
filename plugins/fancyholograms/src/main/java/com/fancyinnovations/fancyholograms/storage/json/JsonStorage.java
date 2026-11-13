package com.fancyinnovations.fancyholograms.storage.json;

import com.fancyinnovations.fancyholograms.api.FancyHolograms;
import com.fancyinnovations.fancyholograms.api.data.BlockHologramData;
import com.fancyinnovations.fancyholograms.api.data.HologramData;
import com.fancyinnovations.fancyholograms.api.data.ItemHologramData;
import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import com.fancyinnovations.fancyholograms.storage.HologramStorage;
import com.fancyinnovations.fancyholograms.storage.json.model.JsonDataUnion;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.jdb.JDB;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

public class JsonStorage implements HologramStorage {

    private static final String DATA_DIR_PATH = "plugins/FancyHolograms/data/holograms";
    private static final File DATA_DIR = new File(DATA_DIR_PATH);
    private final File dataDirectory;
    private final JDB jdb;
    private final Set<HologramData> invalidatedHolograms = Collections.newSetFromMap(new WeakHashMap<>());

    public JsonStorage() {
        this(DATA_DIR);
    }

    public JsonStorage(File dataDirectory) {
        this.dataDirectory = dataDirectory;
        this.jdb = new JDB(dataDirectory.getPath());
    }

    @Override
    public synchronized void saveBatch(Collection<HologramData> holograms) {
        for (HologramData hologram : holograms) {
            save(hologram);
        }
    }

    @Override
    public synchronized void save(HologramData hologram) {
        if (invalidatedHolograms.contains(hologram)) {
            var registered = FancyHolograms.get().getRegistry().get(hologram.getName());
            if (registered.isEmpty() || registered.get().getData() != hologram) {
                return;
            }
            invalidatedHolograms.remove(hologram);
        }

        if (hologram.getFilePath() == null || hologram.getFilePath().isEmpty()) {
            FancyHolograms.get().getFancyLogger().error("Hologram " + hologram.getName() + " has no file path set");
            return;
        }

        JsonDataUnion union = switch (hologram.getType()) {
            case TEXT -> JsonAdapter.toUnion((TextHologramData) hologram);
            case ITEM -> JsonAdapter.toUnion((ItemHologramData) hologram);
            case BLOCK -> JsonAdapter.toUnion((BlockHologramData) hologram);
        };

        try {
            JsonDataUnion[] existing = jdb.get(hologram.getFilePath(), JsonDataUnion[].class);
            if (existing == null) {
                existing = new JsonDataUnion[0];
            }
            for (int i = 0; i < existing.length; i++) {
                JsonDataUnion u = existing[i];
                if (u.hologram_data().name().equals(hologram.getName())) {
                    existing[i] = union;
                    jdb.set(hologram.getFilePath(), existing);
                    hologram.getTraitTrait().save();
                    return;
                }
            }

            JsonDataUnion[] newArray = new JsonDataUnion[existing.length + 1];
            System.arraycopy(existing, 0, newArray, 0, existing.length);
            newArray[existing.length] = union;

            jdb.set(hologram.getFilePath(), newArray);
        } catch (IOException e) {
            FancyHolograms.get().getFancyLogger().error("Failed to save hologram " + hologram.getName(), ThrowableProperty.of(e));
        }

        hologram.getTraitTrait().save();
    }

    @Override
    public synchronized void delete(HologramData hologram) {
        invalidate(hologram);

        if (hologram.getFilePath() == null || hologram.getFilePath().isEmpty()) {
            return;
        }

        try {
            JsonDataUnion[] existing = jdb.get(hologram.getFilePath(), JsonDataUnion[].class);
            if (existing == null) {
                return;
            }

            ArrayList<JsonDataUnion> newArray = new ArrayList<>();
            for (JsonDataUnion u : existing) {
                if (u.hologram_data().name().equals(hologram.getName())) {
                    continue;
                }
                newArray.add(u);
            }

            if (newArray.size() == existing.length) {
                FancyHolograms.get().getFancyLogger().warn("Hologram " + hologram.getName() + " not found in file " + hologram.getFilePath());
                return;
            }

            if (newArray.isEmpty()) {
                jdb.delete(hologram.getFilePath());
                return;
            }

            jdb.set(hologram.getFilePath(), newArray.toArray(new JsonDataUnion[0]));
        } catch (IOException e) {
            FancyHolograms.get().getFancyLogger().error("Failed to save hologram " + hologram.getName(), ThrowableProperty.of(e));
        }
    }

    @Override
    public synchronized void invalidate(HologramData hologram) {
        invalidatedHolograms.add(hologram);
    }

    @Override
    public synchronized Collection<HologramData> loadAll(String path) {
        try {
            return loadAll(path, false);
        } catch (IOException e) {
            FancyHolograms.get().getFancyLogger().error("Failed to load all holograms from " + path, ThrowableProperty.of(e));
            return List.of();
        }
    }

    public synchronized Collection<HologramData> loadAllStrict() throws IOException {
        return loadAll("", true);
    }

    private Collection<HologramData> loadAll(String path, boolean strict) throws IOException {
        List<HologramData> holograms = new ArrayList<>();

        File dir = new File(dataDirectory, path);
        if (!dir.isDirectory()) {
            if (strict && dir.exists()) {
                throw new IOException("Hologram path is not a directory: " + dir);
            }
            return holograms;
        }

        File[] files = dir.listFiles();
        if (files == null) {
            if (strict) {
                throw new IOException("Could not list hologram directory: " + dir);
            }
            return holograms;
        }

        for (File file : files) {
            String fileName = file.getName();

            if (file.isDirectory()) {
                holograms.addAll(loadAll(path + "/" + fileName, strict));
                continue;
            }

            // Skip hidden files
            if (fileName.startsWith(".") || fileName.startsWith("_")) {
                continue;
            }

            // Check if the file is a JSON file
            if (fileName.endsWith(".json")) {
                holograms.addAll(loadFile(path + "/" + fileName.substring(0, fileName.length() - 5), strict));
            } else {
                FancyHolograms.get().getFancyLogger().warn("File " + fileName + " is not a valid hologram file");
            }
        }

        return holograms;
    }

    private Collection<HologramData> loadFile(String path, boolean strict) throws IOException {
        migrate1(path);

        List<HologramData> holograms = new ArrayList<>();

        try {
            JsonDataUnion[] allTextUnions = jdb.get(path, JsonDataUnion[].class);
            if (allTextUnions == null) {
                if (strict) {
                    throw new IOException("Hologram file is empty: " + path);
                }
                FancyHolograms.get().getFancyLogger().debug("File " + path + " is empty or does not exist");
                return holograms;
            }
            for (JsonDataUnion union : allTextUnions) {
                HologramData data = JsonAdapter.fromJson(union);
                data.setFilePath(path);
                holograms.add(data);
            }
        } catch (IOException e) {
            if (strict) {
                throw e;
            }
            FancyHolograms.get().getFancyLogger().error("Failed to load all holograms from " + path, ThrowableProperty.of(e));
        }

        return holograms;
    }

    /**
     * Fixes snakeCase format in hologram data fields.
     *
     * @param path the path to the hologram file
     */
    private void migrate1(String path) {
        FancyHologramsPlugin.get().getFancyLogger().info("Running migration1 for '" + path + "'");

        File file = new File(dataDirectory, path + ".json");
        if (!file.exists()) {
            return;
        }

        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            String newContent = content.replace("\"worldName\":", "\"world_name\":");
            newContent = newContent.replace("\"visibilityDistance\":", "\"visibility_distance\":");
            newContent = newContent.replace("\"linkedNpcName\":", "\"linked_npc_name\":");

            if (!content.equals(newContent)) {
                Files.write(file.toPath(), newContent.getBytes());
                System.out.println("WOHOHOHOHOOH");
            }
        } catch (IOException e) {
            FancyHologramsPlugin.get().getFancyLogger().error("Failed to replace all in file " + path, ThrowableProperty.of(e));
        }

        FancyHologramsPlugin.get().getFancyLogger().info("Migration1 for '" + path + "' completed");
    }
}
