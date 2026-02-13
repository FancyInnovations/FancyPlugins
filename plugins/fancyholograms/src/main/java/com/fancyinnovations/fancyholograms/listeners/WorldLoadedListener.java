package com.fancyinnovations.fancyholograms.listeners;

import com.fancyinnovations.fancyholograms.api.data.HologramData;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldLoadedListener implements Listener {

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        for (Hologram hologram : FancyHologramsPlugin.get().getRegistry().getAll()) {
            HologramData data = hologram.getData();

            if (data.getLocation().getWorld() == null && data.getWorldName().equals(event.getWorld().getName())) {
                data.setWorld(event.getWorld());
            }
        }
    }

    @EventHandler
    public void onWorldUnload(org.bukkit.event.world.WorldUnloadEvent event) {
        for (Hologram hologram : FancyHologramsPlugin.get().getRegistry().getAll()) {
            HologramData data = hologram.getData();

            if (data.getLocation().getWorld() != null && data.getLocation().getWorld().getName().equals(event.getWorld().getName())) {
                data.setWorld(null);
            }
        }
    }

}
