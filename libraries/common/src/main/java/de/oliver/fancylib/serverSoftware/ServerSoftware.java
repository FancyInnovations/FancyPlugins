package de.oliver.fancylib.serverSoftware;

import de.oliver.fancylib.serverSoftware.schedulers.BukkitScheduler;
import de.oliver.fancylib.serverSoftware.schedulers.FancyScheduler;
import de.oliver.fancylib.serverSoftware.schedulers.FoliaScheduler;
import io.papermc.paper.plugin.configuration.PluginMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ServerSoftware {

    private static Boolean isFolia;
    private static Boolean isPaper;
    private static Boolean isBukkit;

    public static boolean isFolia() {
        if (isFolia == null) {
            isFolia = Arrays.stream(PluginMeta.class.getDeclaredMethods())
                .map(Method::getName)
                .anyMatch(s -> s.equals("isFoliaSupported"));
        }

        return isFolia;
    }

    public static boolean isPaper() {
        if (isPaper == null) {
            try {
                Class.forName("io.papermc.paper.event.player.AsyncChatEvent");
                isPaper = true;
            } catch (ClassNotFoundException e) {
                isPaper = false;
            }
        }

        return isPaper;
    }

    public static boolean isBukkit() {
        if (isBukkit == null) {
            try {
                Class.forName("org.bukkit.Bukkit");
                isBukkit = true;
            } catch (ClassNotFoundException e) {
                isBukkit = false;
            }
        }

        return isBukkit;
    }

    public static FancyScheduler getCorrectScheduler(JavaPlugin plugin) {
        if (isFolia()) {
            return new FoliaScheduler(plugin);
        }

        return new BukkitScheduler(plugin);
    }

}
