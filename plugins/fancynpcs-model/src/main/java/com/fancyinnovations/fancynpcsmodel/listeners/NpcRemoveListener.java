package com.fancyinnovations.fancynpcsmodel.listeners;

import com.fancyinnovations.fancynpcsmodel.fancynpcshook.CustomModelAttribute;
import de.oliver.fancynpcs.api.events.NpcRemoveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NpcRemoveListener implements Listener {

    @EventHandler
    public void onNpcRemove(NpcRemoveEvent event) {
        if (!CustomModelAttribute.hasAttribute(event.getNpc())) {
            return;
        }

        CustomModelAttribute.closeAllTrackers(event.getNpc());
    }

}
