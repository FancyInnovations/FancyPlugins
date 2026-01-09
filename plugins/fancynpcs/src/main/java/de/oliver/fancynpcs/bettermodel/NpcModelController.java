package de.oliver.fancynpcs.bettermodel;

import de.oliver.fancynpcs.api.Npc;
import kr.toxicity.model.api.animation.AnimationModifier;
import kr.toxicity.model.api.data.renderer.ModelRenderer;
import kr.toxicity.model.api.tracker.EntityHideOption;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.EntityTrackerRegistry;
import kr.toxicity.model.api.tracker.TrackerModifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class NpcModelController {

    private final Map<String, EntityTracker> trackers = new ConcurrentHashMap<>();
    private final Map<Integer, Npc> entityIdToNpc = new ConcurrentHashMap<>();

    public boolean applyModel(Npc npc, String modelName) {
        if (!BetterModelHook.isAvailable()) {
            return false;
        }

        Optional<ModelRenderer> modelOpt = BetterModelHook.getModel(modelName);
        if (modelOpt.isEmpty()) {
            return false;
        }

        removeModel(npc);

        Entity entity = npc.getEntity();
        if (entity == null) {
            return false;
        }

        EntityTracker tracker = modelOpt.get().create(
                entity,
                TrackerModifier.builder()
                        .sightTrace(true)
                        .damageAnimation(true)
                        .damageTint(true)
                        .build()
        );

        tracker.hideOption(EntityHideOption.DEFAULT);

        String npcId = npc.getData().getId();
        trackers.put(npcId, tracker);
        entityIdToNpc.put(npc.getEntityId(), npc);
        npc.getData().setModelName(modelName);

        EntityTrackerRegistry registry = tracker.registry();
        for (Player player : Bukkit.getOnlinePlayers()) {
            registry.spawn(player);
        }

        return true;
    }

    public void removeModel(Npc npc) {
        if (!BetterModelHook.isAvailable()) {
            return;
        }

        String npcId = npc.getData().getId();
        EntityTracker tracker = trackers.remove(npcId);
        entityIdToNpc.remove(npc.getEntityId());

        if (tracker != null && !tracker.isClosed()) {
            tracker.close();
        }

        npc.getData().setModelName(null);
    }

    public void playAnimation(Npc npc, String animationName) {
        playAnimation(npc, animationName, AnimationModifier.DEFAULT_WITH_PLAY_ONCE);
    }

    public void playAnimation(Npc npc, String animationName, AnimationModifier modifier) {
        if (!BetterModelHook.isAvailable()) {
            return;
        }

        EntityTracker tracker = trackers.get(npc.getData().getId());
        if (tracker != null && !tracker.isClosed()) {
            tracker.animate(animationName, modifier);
        }
    }

    public void spawnForPlayer(Npc npc, Player player) {
        if (!BetterModelHook.isAvailable()) {
            return;
        }

        String modelName = npc.getData().getModelName();
        if (modelName == null) {
            return;
        }

        String npcId = npc.getData().getId();
        EntityTracker tracker = trackers.get(npcId);

        if (tracker == null || tracker.isClosed()) {
            Entity entity = npc.getEntity();
            if (entity == null) {
                return;
            }

            Optional<EntityTrackerRegistry> registryOpt = BetterModelHook.getRegistry(entity);
            if (registryOpt.isEmpty()) {
                applyModel(npc, modelName);
                tracker = trackers.get(npcId);
            } else {
                tracker = registryOpt.get().first();
                if (tracker != null) {
                    trackers.put(npcId, tracker);
                    entityIdToNpc.put(npc.getEntityId(), npc);
                }
            }
        }

        if (tracker != null && !tracker.isClosed()) {
            tracker.markPlayerForSpawn(player);
            tracker.registry().spawn(player);
        }
    }

    public EntityTracker getTracker(Npc npc) {
        return trackers.get(npc.getData().getId());
    }

    public Npc getNpcByEntityId(int entityId) {
        return entityIdToNpc.get(entityId);
    }

    public Npc getNpcByEntity(Entity entity) {
        if (entity == null) {
            return null;
        }
        return entityIdToNpc.get(entity.getEntityId());
    }

    public void cleanupNpc(Npc npc) {
        String npcId = npc.getData().getId();
        EntityTracker tracker = trackers.remove(npcId);
        entityIdToNpc.remove(npc.getEntityId());

        if (tracker != null && !tracker.isClosed()) {
            tracker.close();
        }
    }
}
