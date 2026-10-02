package de.oliver.fancyholograms.listeners;

import de.oliver.fancyholograms.FancyHolograms;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.jetbrains.annotations.NotNull;

public class WorldListener implements Listener {

    private final @NotNull FancyHolograms plugin;
    private final boolean hologramLoadLogging;

    public WorldListener(@NotNull FancyHolograms plugin) {
        this.plugin = plugin;
        hologramLoadLogging = plugin.getHologramConfiguration().isHologramLoadLogging();
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        plugin.getHologramThread().submit(() -> {
            if (hologramLoadLogging) plugin.getFancyLogger().info("Loading holograms for world " + event.getWorld().getName());
            plugin.getHologramsManager().loadHolograms(event.getWorld().getName());
        });
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        plugin.getHologramThread().submit(() -> {
            if (hologramLoadLogging) plugin.getFancyLogger().info("Unloading holograms for world " + event.getWorld().getName());
            plugin.getHologramsManager().unloadHolograms(event.getWorld().getName());
        });
    }

}
