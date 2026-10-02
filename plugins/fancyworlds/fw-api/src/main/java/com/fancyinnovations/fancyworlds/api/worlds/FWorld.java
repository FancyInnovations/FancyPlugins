package com.fancyinnovations.fancyworlds.api.worlds;

import org.bukkit.World;

import java.util.UUID;

public interface FWorld {

    UUID getID();

    String getName();

    /**
     * Renaming worlds is not currently supported because it requires moving the
     * world's files and updating Paper's world identity.
     *
     * @throws UnsupportedOperationException always
     */
    void rename(String newName);

    long getSeed();

    World.Environment getEnvironment();

    String getGenerator();

    boolean canGenerateStructures();

    FWorldSettings getSettings();

    void setSettings(FWorldSettings settings);

    boolean isWorldLoaded();

    boolean isWorldOnDisk();

    World getBukkitWorld();

}
