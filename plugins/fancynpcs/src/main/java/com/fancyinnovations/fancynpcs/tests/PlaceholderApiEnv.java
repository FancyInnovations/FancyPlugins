package com.fancyinnovations.fancynpcs.tests;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderApiEnv extends PlaceholderExpansion {
    public static int i = 0;
    public static String[] strings = new String[]{
            "Grabsky",
            "OakLoaf",
            "GommeHD",
    };

    public static void registerPlaceholders() {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return;
        }

        PlaceholderExpansion expansion = new PlaceholderApiEnv();
        expansion.register();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "fn-test";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Oliver";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        i++;
        if (i >= strings.length) {
            i = 0;
        }
        return strings[i];
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        i++;
        if (i >= strings.length) {
            i = 0;
        }
        return strings[i];
    }

}
