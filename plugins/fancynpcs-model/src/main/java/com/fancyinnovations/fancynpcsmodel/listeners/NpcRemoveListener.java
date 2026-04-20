package com.fancyinnovations.fancynpcsmodel.listeners;

import com.fancyinnovations.fancynpcsmodel.fancynpcshook.ModelAttribute;
import de.oliver.fancynpcs.api.events.NpcRemoveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NpcRemoveListener implements Listener {

    @EventHandler
    public void onNpcRemove(NpcRemoveEvent event) {
        if (!ModelAttribute.hasAttribute(event.getNpc())) {
            return;
        }

        ModelAttribute.closeAllTrackers(event.getNpc());
    }

}
