package com.fancyinnovations.fancyworlds.utils;

import org.bukkit.Bukkit;

import java.nio.file.Files;
import java.nio.file.Path;

public class WorldFileUtils {

    public static boolean isWorldOnDisk(String worldName) {
        return Files.exists(getWorldDirectory(worldName));
    }

    public static Path getWorldDirectory(String worldName) {
        Path legacy = Bukkit.getWorldContainer().toPath().resolve(worldName);
        if (Files.exists(legacy) || Bukkit.getWorlds().isEmpty()) return legacy;

        Path dimensions = Bukkit.getWorldContainer().toPath()
                .resolve(Bukkit.getWorlds().getFirst().getName())
                .resolve("dimensions").resolve("minecraft").resolve(worldName);

        return Files.exists(dimensions) ? dimensions : legacy;
    }

}
