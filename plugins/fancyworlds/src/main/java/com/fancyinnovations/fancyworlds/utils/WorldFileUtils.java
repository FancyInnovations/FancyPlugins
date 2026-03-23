package com.fancyinnovations.fancyworlds.utils;

import org.bukkit.Bukkit;

public class WorldFileUtils {

    public static boolean isWorldOnDisk(String worldName) {
        return Bukkit.getWorldContainer().toPath()
                .resolve(worldName).toFile()
                .exists();
    }

}
