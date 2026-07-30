package com.fancyinnovations.fancynpcsmodel.listeners;

import com.fancyinnovations.fancynpcsmodel.fancynpcshook.CustomModelAttribute;
import de.oliver.fancynpcs.api.events.NpcRemoveEvent;
import de.oliver.fancynpcs.api.events.NpcUnloadEvent;
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

    /**
     * Fired when /fancynpcs reload discards a npc instance (without deleting it) to
     * recreate it from scratch. The old model tracker would otherwise never be closed,
     * leaking as an orphaned tracker in BetterModel's registry - and, unlike a normal
     * per-player despawn, this leftover tracker has no npc left to be re-synced through
     * NpcSpawnEvent/NpcDespawnEvent, since the npc instance it belonged to is gone.
     */
    @EventHandler
    public void onNpcUnload(NpcUnloadEvent event) {
        if (!CustomModelAttribute.hasAttribute(event.getNpc())) {
            return;
        }

        CustomModelAttribute.closeAllTrackers(event.getNpc());
    }

}
