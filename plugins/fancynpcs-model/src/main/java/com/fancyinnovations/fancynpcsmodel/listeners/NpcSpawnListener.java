package com.fancyinnovations.fancynpcsmodel.listeners;

import com.fancyinnovations.fancynpcsmodel.fancynpcshook.CustomModelAttribute;
import de.oliver.fancynpcs.api.events.NpcSpawnEvent;
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter;
import kr.toxicity.model.api.tracker.EntityTracker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NpcSpawnListener implements Listener {

    @EventHandler
    public void onNpcSpawn(NpcSpawnEvent event) {
        if (!CustomModelAttribute.hasAttribute(event.getNpc())) {
            return;
        }

        EntityTracker tracker = CustomModelAttribute.getEntityTracker(event.getNpc());
        if (tracker == null) {
            return;
        }

        // Re-attach the model for this player whenever the underlying npc (re)spawns for them.
        // Needed because FancyNpcs despawns/respawns the npc per player based on distance
        // (and does an extra respawn on Folia to fix its own visibility bug), but the model
        // tracker is otherwise only ever spawned once, when the attribute is first set.
        //
        // Use the unconditional spawn() (not spawnIfNotSpawned()): FancyNpcs never fires any
        // event when it despawns the npc for a player, so BetterModel's own per-player "spawned"
        // state is never told the player left range and stays stale/true. spawnIfNotSpawned()
        // would then silently no-op here instead of resending the model packets.
        CustomModelAttribute.runOnPlayerScheduler(event.getPlayer(), () ->
                tracker.registry().spawn(BukkitAdapter.adapt(event.getPlayer())));
    }

}
