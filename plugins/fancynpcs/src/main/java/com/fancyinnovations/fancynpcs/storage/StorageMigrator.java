package com.fancyinnovations.fancynpcs.storage;

import com.fancyinnovations.fancynpcs.FancyNpcs;
import com.fancyinnovations.fancynpcs.api.Npc;

import java.io.File;
import java.util.Collection;

/**
 * Migrates NPCs from the old YAML format (npcs.yml) to the new JSON format.
 */
public class StorageMigrator {

    private static final File NPCS_YML_FILE = new File("plugins/FancyNpcs/npcs.yml");

    public void migrate() {
        if (!NPCS_YML_FILE.exists()) {
            FancyNpcs.getInstance().getFancyLogger().debug("No npcs.yml file found, skipping migration.");
            return;
        }

        FancyNpcs.getInstance().getFancyLogger().info("Migrating npcs.yml to JSON format...");

        // Load all NPCs from the old YAML storage
        FlatFileNpcStorage yamlStorage = new FlatFileNpcStorage(FancyNpcs.getInstance().getNpcAdapter());
        Collection<Npc> npcs = yamlStorage.loadAll();

        // Save each NPC to the new JSON storage (one file per NPC in migrated folder)
        for (Npc npc : npcs) {
            npc.getData().setFilePath("migrated/" + npc.getData().getName());
            npc.setDirty(true);
            FancyNpcs.getInstance().getNpcStorage().save(npc);
            FancyNpcs.getInstance().getFancyLogger().info("Migrated NPC: " + npc.getData().getName());
        }

        // Rename the old YAML file
        File oldFile = NPCS_YML_FILE.getParentFile().toPath().resolve("npcs-old.yml").toFile();
        if (!NPCS_YML_FILE.renameTo(oldFile)) {
            FancyNpcs.getInstance().getFancyLogger().error("Failed to rename npcs.yml to npcs-old.yml");
        }

        FancyNpcs.getInstance().getFancyLogger().info("Migration completed. Migrated " + npcs.size() + " NPCs.");
    }
}
