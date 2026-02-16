package de.oliver.fancyvisuals.tablist;

import de.oliver.fancyvisuals.FancyVisuals;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TablistListeners implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!FancyVisuals.get().getFancyVisualsConfig().isTablistEnabled()) {
            return;
        }

        Player player = event.getPlayer();
        FancyVisuals.get().getTablistManager().handleJoin(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!FancyVisuals.get().getFancyVisualsConfig().isTablistEnabled()) {
            return;
        }

        Player player = event.getPlayer();
        FancyVisuals.get().getTablistManager().handleQuit(player);
    }

    @EventHandler
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        if (!FancyVisuals.get().getFancyVisualsConfig().isTablistEnabled()) {
            return;
        }

        Player player = event.getPlayer();
        FancyVisuals.get().getTablistManager().handleContextChange(player);
    }
}
