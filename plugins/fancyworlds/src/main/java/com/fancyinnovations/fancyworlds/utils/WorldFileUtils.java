package com.fancyinnovations.fancyworlds.utils;

import org.bukkit.Bukkit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class WorldFileUtils {

    public static boolean isWorldOnDisk(String worldName) {
        Path legacy = Bukkit.getWorldContainer().toPath().resolve(worldName);
        if (hasExactLegacyWorldData(legacy)) return true;

        return hasDimensionWorldData(getDimensionDirectory(worldName));
    }

    public static Path getWorldDirectory(String worldName) {
        Path legacy = Bukkit.getWorldContainer().toPath().resolve(worldName);
        if (hasExactLegacyWorldData(legacy) || Bukkit.getWorlds().isEmpty()) return legacy;

        Path dimensions = getDimensionDirectory(worldName);
        return Files.exists(dimensions) ? dimensions : legacy;
    }

    public static String findWorldNameOnDisk(String input) {
        List<String> names = availableWorldNames();
        if (names.contains(input)) return input;

        List<String> matches = names.stream()
                .filter(name -> name.equalsIgnoreCase(input))
                .toList();

        return matches.size() == 1 ? matches.getFirst() : null;
    }

    public static List<String> availableWorldNames() {
        List<String> names = new ArrayList<>();

        Path container = Bukkit.getWorldContainer().toPath();
        try (Stream<Path> paths = Files.list(container)) {
            paths.filter(WorldFileUtils::hasLegacyWorldData)
                    .map(path -> path.getFileName().toString())
                    .forEach(names::add);
        } catch (IOException ignored) {
            // The load command reports that no world was found.
        }

        Path dimensions = getDimensionsRoot();
        if (dimensions != null && Files.isDirectory(dimensions)) {
            try (Stream<Path> paths = Files.list(dimensions)) {
                paths.filter(WorldFileUtils::hasDimensionWorldData)
                        .map(path -> bukkitNameForDimension(path.getFileName().toString()))
                        .filter(name -> !names.contains(name))
                        .forEach(names::add);
            } catch (IOException ignored) {
                // Legacy worlds can still be suggested.
            }
        }

        return names;
    }

    private static boolean hasLegacyWorldData(Path path) {
        return Files.isRegularFile(path.resolve("level.dat")) || Files.isRegularFile(path.resolve("level.dat_old"));
    }

    private static boolean hasExactLegacyWorldData(Path path) {
        if (!hasLegacyWorldData(path)) return false;

        try (Stream<Path> entries = Files.list(path.getParent())) {
            return entries.anyMatch(entry -> entry.getFileName().equals(path.getFileName()));
        } catch (IOException ignored) {
            return false;
        }
    }

    private static boolean hasDimensionWorldData(Path path) {
        return Files.isRegularFile(path.resolve("data/minecraft/world_gen_settings.dat"));
    }

    private static Path getDimensionDirectory(String worldName) {
        Path root = getDimensionsRoot();
        if (root == null) return Bukkit.getWorldContainer().toPath().resolve(worldName);

        return root.resolve(dimensionNameForWorld(worldName));
    }

    private static Path getDimensionsRoot() {
        if (Bukkit.getWorlds().isEmpty()) return null;

        return Bukkit.getWorldContainer().toPath()
                .resolve(Bukkit.getUnsafe().getMainLevelName())
                .resolve("dimensions").resolve("minecraft");
    }

    private static String dimensionNameForWorld(String worldName) {
        String mainName = Bukkit.getUnsafe().getMainLevelName();
        if (worldName.equalsIgnoreCase(mainName)) return "overworld";
        if (worldName.equalsIgnoreCase(mainName + "_nether")) return "the_nether";
        if (worldName.equalsIgnoreCase(mainName + "_the_end")) return "the_end";
        return worldName.toLowerCase(Locale.ROOT);
    }

    private static String bukkitNameForDimension(String dimensionName) {
        String mainName = Bukkit.getUnsafe().getMainLevelName();
        return switch (dimensionName) {
            case "overworld" -> mainName;
            case "the_nether" -> mainName + "_nether";
            case "the_end" -> mainName + "_the_end";
            default -> dimensionName;
        };
    }

}
