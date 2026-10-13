package de.oliver.fancyvisuals.nametags.listeners;

import de.oliver.fancyvisuals.FancyVisuals;
import de.oliver.fancyvisuals.api.nametags.Nametag;
import de.oliver.fancyvisuals.nametags.visibility.PlayerNametag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class NametagListeners implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Nametag nametag = FancyVisuals.get().getNametagRepository().getNametagForPlayer(player);
        PlayerNametag playerNametag = new PlayerNametag(nametag, player);
        FancyVisuals.get().getNametagScheduler().add(playerNametag);
    }

    @EventHandler
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        Nametag nametag = FancyVisuals.get().getNametagRepository().getNametagForPlayer(player);
        PlayerNametag playerNametag = new PlayerNametag(nametag, player);
        FancyVisuals.get().getNametagScheduler().add(playerNametag);
    }
}
