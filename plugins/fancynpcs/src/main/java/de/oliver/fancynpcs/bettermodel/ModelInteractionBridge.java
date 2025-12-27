package de.oliver.fancynpcs.bettermodel;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.api.events.NpcRemoveEvent;
import de.oliver.fancynpcs.api.events.NpcSpawnEvent;
import de.oliver.fancynpcs.api.events.NpcsLoadedEvent;
import kr.toxicity.model.api.event.ModelInteractEvent;
import kr.toxicity.model.api.nms.ModelInteractionHand;
import kr.toxicity.model.api.tracker.EntityTracker;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ModelInteractionBridge implements Listener {

    private final NpcModelController modelController;

    public ModelInteractionBridge(NpcModelController modelController) {
        this.modelController = modelController;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onModelInteract(ModelInteractEvent event) {
        Entity sourceEntity = event.getHitBox().source();
        Npc npc = modelController.getNpcByEntity(sourceEntity);

        if (npc == null) {
            npc = FancyNpcs.getInstance().getNpcManager().getNpc(sourceEntity.getEntityId());
        }

        if (npc == null) {
            return;
        }

        ActionTrigger trigger = event.getHand() == ModelInteractionHand.LEFT
                ? ActionTrigger.LEFT_CLICK
                : ActionTrigger.RIGHT_CLICK;

        npc.interact(event.getPlayer(), trigger);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNpcModify(NpcModifyEvent event) {
        if (event.getModification() != NpcModifyEvent.NpcModification.LOCATION
                && event.getModification() != NpcModifyEvent.NpcModification.ROTATION) {
            return;
        }

        Npc npc = event.getNpc();
        String modelName = npc.getData().getModelName();
        if (modelName == null) {
            return;
        }

        FancyNpcs.getInstance().getScheduler().runTaskLater(null, 1L, () -> {
            modelController.removeModel(npc);
            npc.getData().setModelName(modelName);
            modelController.applyModel(npc, modelName);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNpcRemove(NpcRemoveEvent event) {
        Npc npc = event.getNpc();
        if (npc.getData().getModelName() != null) {
            modelController.cleanupNpc(npc);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNpcSpawn(NpcSpawnEvent event) {
        Npc npc = event.getNpc();
        String modelName = npc.getData().getModelName();

        if (modelName == null) {
            return;
        }

        FancyNpcs.getInstance().getScheduler().runTaskLater(null, 10L, () -> {
            EntityTracker tracker = modelController.getTracker(npc);
            if (tracker == null || tracker.isClosed()) {
                modelController.applyModel(npc, modelName);
            }
            modelController.spawnForPlayer(npc, event.getPlayer());
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onNpcsLoaded(NpcsLoadedEvent event) {
        for (Npc npc : FancyNpcs.getInstance().getNpcManager().getAllNpcs()) {
            String modelName = npc.getData().getModelName();
            if (modelName != null && !modelName.isEmpty()) {
                modelController.applyModel(npc, modelName);
            }
        }
    }
}
