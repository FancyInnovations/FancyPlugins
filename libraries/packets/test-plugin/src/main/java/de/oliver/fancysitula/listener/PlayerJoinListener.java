package de.oliver.fancysitula.listener;

import de.oliver.fancysitula.FancySitulaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        FancySitulaPlugin.getInstance().getPacketListener().inject(event.getPlayer());
    }

}
