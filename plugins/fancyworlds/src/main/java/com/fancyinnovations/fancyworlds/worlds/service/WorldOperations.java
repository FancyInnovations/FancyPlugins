package com.fancyinnovations.fancyworlds.worlds.service;

import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.api.worlds.WorldService;
import com.fancyinnovations.fancyworlds.utils.WorldFileUtils;
import com.fancyinnovations.fancyworlds.worlds.FWorldImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

public final class WorldOperations {

    private WorldOperations() {
    }

    public static boolean load(FWorldImpl world) {
        if (world.isWorldLoaded() || !world.isWorldOnDisk()) return false;
        WorldService service = WorldService.get();
        boolean newlyLinked = service.getWorldByName(world.getName()) == null;
        // The load event fires before createWorld returns and must resolve to this same FWorld.
        service.registerWorld(world);

        World loaded;
        try {
            loaded = world.toWorldCreator().createWorld();
        } catch (RuntimeException ex) {
            if (newlyLinked) service.unregisterWorld(world);
            return false;
        }

        if (loaded == null) {
            if (newlyLinked) service.unregisterWorld(world);
            return false;
        }

        world.setBukkitWorld(loaded);
        service.registerWorld(world);

        return true;
    }

    public static boolean unload(FWorldImpl world) {
        if (!world.isWorldLoaded() || world.getBukkitWorld().getPlayerCount() != 0) {
            return false;
        }
        if (!Bukkit.unloadWorld(world.getBukkitWorld(), true)) {
            return false;
        }

        world.setBukkitWorld(null);
        return true;
    }

    public static boolean setSpawn(FWorld world, Location location) {
        if (!world.isWorldLoaded() || location.getWorld() != world.getBukkitWorld()) {
            return false;
        }

        world.getBukkitWorld().setSpawnLocation(location);
        return true;
    }

    public static void delete(FWorld world) throws IOException {
        if (world.isWorldLoaded()) throw new IOException("World is loaded");

        Path container = Bukkit.getWorldContainer().toPath().toAbsolutePath().normalize();
        Path directory = WorldFileUtils.getWorldDirectory(world.getName()).toAbsolutePath().normalize();
        if (!directory.startsWith(container) || directory.equals(container)) {
            throw new IOException("Invalid world directory");
        }

        try (Stream<Path> paths = Files.walk(directory)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) Files.delete(path);
        }

        WorldService.get().unregisterWorld(world);
    }
}
