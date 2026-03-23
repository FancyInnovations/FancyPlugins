package com.fancyinnovations.fancyworlds.api.worlds;

import org.bukkit.World;

import java.util.UUID;

public interface FWorld {

    UUID getID();

    String getName();

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
