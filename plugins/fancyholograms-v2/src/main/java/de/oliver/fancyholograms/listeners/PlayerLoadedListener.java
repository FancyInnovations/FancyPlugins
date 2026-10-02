package de.oliver.fancyholograms.listeners;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.hologram.Hologram;
import io.papermc.paper.event.player.PlayerClientLoadedWorldEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class PlayerLoadedListener implements Listener {
    private final @NotNull FancyHolograms plugin;

    public PlayerLoadedListener(@NotNull FancyHolograms plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLoaded(@NotNull final PlayerClientLoadedWorldEvent event) {
        plugin.getHologramThread().submit(() -> {
            for (final Hologram hologram : plugin.getHologramsManager().getHolograms()) {
                hologram.forceUpdateShownStateFor(event.getPlayer());
            }
        });
    }

}
