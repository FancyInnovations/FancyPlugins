package com.fancyinnovations.fancynpcsmodel.listeners;

import com.fancyinnovations.fancynpcsmodel.fancynpcshook.ModelAttribute;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NpcInteractListener implements Listener {

    @EventHandler
    public void onNpcInteract(NpcInteractEvent event) {
        if (!ModelAttribute.hasAttribute(event.getNpc())) {
            return;
        }

        event.setCancelled(true);
    }

}
