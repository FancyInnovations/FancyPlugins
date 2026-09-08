package com.fancyinnovations.fancyholograms.storage.json;

import com.fancyinnovations.fancyholograms.api.FancyHolograms;
import com.fancyinnovations.fancyholograms.api.data.BlockHologramData;
import com.fancyinnovations.fancyholograms.api.data.HologramData;
import com.fancyinnovations.fancyholograms.api.data.ItemHologramData;
import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.storage.HologramStorage;
import com.fancyinnovations.fancyholograms.storage.json.model.JsonDataUnion;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.jdb.JDB;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JsonStorage implements HologramStorage {

    private static final String DATA_DIR_PATH = "plugins/FancyHolograms/data/holograms";
    private static final File DATA_DIR = new File(DATA_DIR_PATH);
    private final JDB jdb;

    public JsonStorage() {
        this.jdb = new JDB(DATA_DIR_PATH);
    }

    @Override
    public void saveBatch(Collection<HologramData> holograms) {
        for (HologramData hologram : holograms) {
            save(hologram);
        }
    }

    @Override
    public void save(HologramData hologram) {
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
    public void delete(HologramData hologram) {
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
    public Collection<HologramData> loadAll(String path) {
        List<HologramData> holograms = new ArrayList<>();

        File dir = new File(DATA_DIR, path);
        if (!dir.isDirectory()) {
            return holograms;
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return holograms;
        }

        for (File file : files) {
            String fileName = file.getName();

            if (file.isDirectory()) {
                holograms.addAll(loadAll(path + "/" + fileName));
                continue;
            }

            // Skip hidden files
            if (fileName.startsWith(".") || fileName.startsWith("_")) {
                continue;
            }

            // Check if the file is a JSON file
            if (fileName.endsWith(".json")) {
                holograms.addAll(loadFile(path + "/" + fileName.substring(0, fileName.length() - 5)));
            } else {
                FancyHolograms.get().getFancyLogger().warn("File " + fileName + " is not a valid hologram file");
            }
        }

        return holograms;
    }

    public Collection<HologramData> loadFile(String path) {
        List<HologramData> holograms = new ArrayList<>();

        try {
            JsonDataUnion[] allTextUnions = jdb.get(path, JsonDataUnion[].class);
            if (allTextUnions == null) {
                FancyHolograms.get().getFancyLogger().debug("File " + path + " is empty or does not exist");
                return holograms;
            }
            for (JsonDataUnion union : allTextUnions) {
                HologramData data = JsonAdapter.fromJson(union);
                data.setFilePath(path);
                holograms.add(data);
            }
        } catch (IOException e) {
            FancyHolograms.get().getFancyLogger().error("Failed to load all holograms from " + path, ThrowableProperty.of(e));
        }

        return holograms;
    }
}
