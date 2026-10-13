package com.fancyinnovations.fancynpcs.listeners;

import com.fancyinnovations.fancynpcs.FancyNpcs;
import com.fancyinnovations.fancynpcs.api.Npc;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerChangedWorldListener implements Listener {

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        for (Npc npc : FancyNpcs.getInstance().getNpcManager().getAllNpcs()) {
            npc.checkAndUpdateVisibility(event.getPlayer());
        }
    }

}
