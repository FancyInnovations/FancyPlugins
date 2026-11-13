package com.fancyinnovations.fancyholograms.storage;

import com.fancyinnovations.fancyholograms.api.data.HologramData;

import java.util.Collection;

public interface HologramStorage {

    /**
     * Saves a collection of holograms.
     *
     * @param holograms The holograms to save.
     */
    void saveBatch(Collection<HologramData> holograms);

    /**
     * Saves a hologram.
     *
     * @param hologram The hologram to save.
     */
    void save(HologramData hologram);

    /**
     * Deletes a hologram.
     *
     * @param hologram The hologram to delete.
     */
    void delete(HologramData hologram);

    /**
     * Rejects pending saves of unloaded data without deleting its stored files.
     */
    default void invalidate(HologramData hologram) {
    }

    /**
     * Loads all holograms from the specified path (recursive).
     *
     * @param path The relative path to the plugin/FancyHolograms/data/holograms directory.
     * @return A collection of all loaded holograms.
     */
    Collection<HologramData> loadAll(String path);

    /**
     * Loads all holograms from the default path
     */
    default Collection<HologramData> loadAll() {
        return loadAll("");
    }
}
