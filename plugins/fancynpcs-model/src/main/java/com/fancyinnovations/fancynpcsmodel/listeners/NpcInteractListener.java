package com.fancyinnovations.fancynpcsmodel.listeners;

import com.fancyinnovations.fancynpcsmodel.fancynpcshook.CustomModelAttribute;
import com.fancyinnovations.fancynpcsmodel.providers.ModelProvider;
import com.fancyinnovations.fancynpcsmodel.providers.ModelProviderRegistry;
import de.oliver.fancynpcs.api.events.NpcPreInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NpcInteractListener implements Listener {

    @EventHandler
    public void onNpcInteract(NpcPreInteractEvent event) {
        if (!CustomModelAttribute.hasAttribute(event.getNpc())) {
            return;
        }

        ModelProvider provider = ModelProviderRegistry.getActiveProvider(event.getNpc());
        if (provider != null && provider.shouldCancelNativeInteraction()) {
            event.setCancelled(true);
        }
    }

}
