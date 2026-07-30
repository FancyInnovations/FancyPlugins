package com.fancyinnovations.fancynpcsmodel.listeners;

import com.fancyinnovations.fancynpcsmodel.fancynpcshook.CustomModelAttribute;
import de.oliver.fancynpcs.api.events.NpcDespawnEvent;
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter;
import kr.toxicity.model.api.tracker.EntityTracker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NpcDespawnListener implements Listener {

    @EventHandler
    public void onNpcDespawn(NpcDespawnEvent event) {
        if (!CustomModelAttribute.hasAttribute(event.getNpc())) {
            return;
        }

        EntityTracker tracker = CustomModelAttribute.getEntityTracker(event.getNpc());
        if (tracker == null) {
            return;
        }

        // Keep BetterModel's per-player spawn state in sync with FancyNpcs' own visibility
        // tracking, so the next NpcSpawnEvent for this player reflects a real state change
        // instead of relying purely on an unconditional resend.
        CustomModelAttribute.runOnPlayerScheduler(event.getPlayer(), () ->
                tracker.registry().remove(BukkitAdapter.adapt(event.getPlayer())));
    }

}
