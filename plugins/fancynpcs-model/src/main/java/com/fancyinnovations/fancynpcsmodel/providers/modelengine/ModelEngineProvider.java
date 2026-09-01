package com.fancyinnovations.fancynpcsmodel.providers.modelengine;

import com.fancyinnovations.fancynpcsmodel.main.FancyNpcsModelPlugin;
import com.fancyinnovations.fancynpcsmodel.providers.ModelProvider;
import com.fancyinnovations.fancynpcsmodel.utils.NpcEntityAccess;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.Dummy;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import de.oliver.fancyanalytics.logger.properties.StringProperty;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Model provider backed by the ModelEngine (Model Engine) plugin.
 * <p>
 * FancyNpcs NPCs are packet based and don't exist in the world, so ModelEngine
 * cannot track them like regular entities. Instead, a ModelEngine {@link Dummy}
 * base entity is created at the NPC's location and the model is attached to it.
 * The NPC itself is made invisible while a model is attached. The dummy's
 * location is kept in sync with the NPC every tick.
 */
public class ModelEngineProvider implements ModelProvider {

    private final Map<String, AppliedModel> appliedModels = new HashMap<>(); // npc id -> applied model
    private final Map<UUID, String> dummyToNpc = new HashMap<>(); // dummy uuid -> npc id
    private final Map<String, String> pendingModels = new HashMap<>(); // npc id -> model name of scheduled apply

    @Override
    public String getId() {
        return "modelengine";
    }

    @Override
    public String getDisplayName() {
        return "ModelEngine";
    }

    @Override
    public Collection<String> getPrefixes() {
        return List.of("me", "modelengine", "meg");
    }

    @Override
    public Collection<String> getModelNames() {
        return List.copyOf(ModelEngineAPI.getAPI().getModelRegistry().getOrderedId());
    }

    @Override
    public boolean hasModel(String modelName) {
        return ModelEngineAPI.getBlueprint(modelName) != null;
    }

    @Override
    public synchronized void applyModel(Npc npc, String modelName) {
        String npcId = npc.getData().getId();

        AppliedModel current = appliedModels.get(npcId);
        if (current != null && current.modelName.equalsIgnoreCase(modelName)) {
            // attributes are applied on every npc update - keep the existing model
            hideNpc(npc);
            return;
        }

        if (!hasModel(modelName)) {
            FancyNpcsModelPlugin.get().getFancyLogger().error(
                    "Failed to get ModelEngine model with name " + modelName,
                    StringProperty.of("model_name", modelName),
                    StringProperty.of("npc_name", npc.getData().getName())
            );
            return;
        }

        // hide the npc right away, so the entity metadata that FancyNpcs sends
        // after applying the attributes already contains the invisible flag
        // (the model itself is only created on the next global tick)
        hideNpc(npc);

        // ModelEngine is not thread safe and attributes might be applied from the
        // FancyNpcs npc thread - create the model on the global region thread
        boolean alreadyScheduled = pendingModels.put(npcId, modelName) != null;
        if (alreadyScheduled) {
            return;
        }

        Bukkit.getGlobalRegionScheduler().execute(FancyNpcsModelPlugin.get(), () -> applyPending(npc));
    }

    private synchronized void applyPending(Npc npc) {
        String npcId = npc.getData().getId();
        String modelName = pendingModels.remove(npcId);
        if (modelName == null) {
            return;
        }

        AppliedModel current = appliedModels.get(npcId);
        if (current != null) {
            if (current.modelName.equalsIgnoreCase(modelName)) {
                return;
            }

            appliedModels.remove(npcId);
            dummyToNpc.remove(current.dummy.getUUID());
            teardown(current);
        }

        Location location = npc.getData().getLocation();
        if (location == null || location.getWorld() == null) {
            FancyNpcsModelPlugin.get().getFancyLogger().warn(
                    "Cannot apply ModelEngine model to NPC without a location",
                    StringProperty.of("npc_name", npc.getData().getName())
            );
            restoreNpcVisibility(npc);
            return;
        }

        Dummy<Npc> dummy = new Dummy<>(npc);
        dummy.syncLocation(location.clone());

        ModeledEntity modeledEntity;
        ActiveModel activeModel;
        try {
            modeledEntity = ModelEngineAPI.createModeledEntity(dummy);
            activeModel = ModelEngineAPI.createActiveModel(modelName);
        } catch (Exception e) {
            FancyNpcsModelPlugin.get().getFancyLogger().error(
                    "Failed to create ModelEngine model",
                    ThrowableProperty.of(e),
                    StringProperty.of("model_name", modelName),
                    StringProperty.of("npc_name", npc.getData().getName())
            );
            dummy.setRemoved(true);
            restoreNpcVisibility(npc);
            return;
        }

        if (modeledEntity == null || activeModel == null) {
            FancyNpcsModelPlugin.get().getFancyLogger().error(
                    "Failed to create ModelEngine model",
                    StringProperty.of("model_name", modelName),
                    StringProperty.of("npc_name", npc.getData().getName())
            );
            dummy.setRemoved(true);
            restoreNpcVisibility(npc);
            return;
        }

        // Scale
        double scale = npc.getData().getScale();
        if (scale != 1) {
            activeModel.setScale(scale);
            activeModel.setHitboxScale(scale);
        }

        modeledEntity.addModel(activeModel, true);

        AppliedModel applied = new AppliedModel(npc, modelName, dummy, modeledEntity, activeModel);
        applied.lastSyncedLocation = location.clone();

        // keep the dummy at the NPC's location (e.g. after /npc teleport)
        modeledEntity.registerTickTask(ModeledEntity.Phase.PRE_MODEL_TICK, (Consumer<ModeledEntity>) (me) -> syncLocation(applied));

        appliedModels.put(npcId, applied);
        dummyToNpc.put(dummy.getUUID(), npcId);

        hideNpc(npc);
    }

    @Override
    public synchronized void removeModel(Npc npc) {
        String npcId = npc.getData().getId();
        pendingModels.remove(npcId);

        AppliedModel applied = appliedModels.remove(npcId);
        if (applied == null) {
            return;
        }

        dummyToNpc.remove(applied.dummy.getUUID());
        teardown(applied);
        restoreNpcVisibility(npc);
    }

    @Override
    public synchronized boolean hasModelApplied(Npc npc) {
        String npcId = npc.getData().getId();
        return appliedModels.containsKey(npcId) || pendingModels.containsKey(npcId);
    }

    @Override
    public synchronized boolean playAnimation(Npc npc, String animation, boolean loop) {
        AppliedModel applied = appliedModels.get(npc.getData().getId());
        if (applied == null) {
            return false;
        }

        IAnimationProperty property = applied.activeModel.getAnimationHandler()
                .playAnimation(animation, 0, 0, 1, true);
        if (property == null) {
            return false;
        }

        property.setForceLoopMode(loop ? BlueprintAnimation.LoopMode.LOOP : BlueprintAnimation.LoopMode.ONCE);
        return true;
    }

    @Override
    public synchronized Collection<String> getAnimationNames(Npc npc) {
        AppliedModel applied = appliedModels.get(npc.getData().getId());
        if (applied == null) {
            return List.of();
        }

        return List.copyOf(applied.activeModel.getBlueprint().getAnimations().keySet());
    }

    @Override
    public boolean shouldCancelNativeInteraction() {
        // the NPC entity is only invisible but still clickable - keep FancyNpcs'
        // own interaction handling as fallback for clicks on the NPC hitbox
        return false;
    }

    @Override
    public @Nullable Listener createListener() {
        return new ModelEngineInteractListener(this);
    }

    @Override
    public synchronized void shutdown() {
        for (AppliedModel applied : appliedModels.values()) {
            teardown(applied);
        }

        appliedModels.clear();
        dummyToNpc.clear();
        pendingModels.clear();
    }

    /**
     * @return the NPC that is modeled by the given ModelEngine base entity or null
     */
    public synchronized @Nullable Npc getNpcForBase(@Nullable BaseEntity<?> base) {
        if (base == null) {
            return null;
        }

        String npcId = dummyToNpc.get(base.getUUID());
        if (npcId == null) {
            return null;
        }

        AppliedModel applied = appliedModels.get(npcId);
        return applied != null ? applied.npc : null;
    }

    private void syncLocation(AppliedModel applied) {
        Location target = applied.npc.getData().getLocation();
        if (target == null || target.getWorld() == null) {
            return;
        }

        Location last = applied.lastSyncedLocation;
        if (last != null
                && last.getWorld() == target.getWorld()
                && last.getX() == target.getX()
                && last.getY() == target.getY()
                && last.getZ() == target.getZ()
                && last.getYaw() == target.getYaw()
                && last.getPitch() == target.getPitch()) {
            return;
        }

        applied.lastSyncedLocation = target.clone();
        applied.dummy.syncLocation(target.clone());
    }

    private void teardown(AppliedModel applied) {
        try {
            applied.dummy.setRemoved(true);
            if (!applied.modeledEntity.isDestroyed()) {
                applied.modeledEntity.destroy();
            }
            ModelEngineAPI.removeModeledEntity(applied.dummy.getUUID());
        } catch (Exception e) {
            FancyNpcsModelPlugin.get().getFancyLogger().warn(
                    "Failed to remove ModelEngine model",
                    ThrowableProperty.of(e),
                    StringProperty.of("npc_name", applied.npc.getData().getName())
            );
        }
    }

    private void hideNpc(Npc npc) {
        Entity entity = NpcEntityAccess.getBukkitEntity(npc);
        if (entity != null) {
            entity.setInvisible(true);
        }
    }

    private void restoreNpcVisibility(Npc npc) {
        NpcAttribute invisibleAttribute = FancyNpcsPlugin.get().getAttributeManager().getAttributeByName(EntityType.PLAYER, "invisible");
        boolean userInvisible = invisibleAttribute != null
                && npc.getData().getAttributes().getOrDefault(invisibleAttribute, "false").equalsIgnoreCase("true");
        if (userInvisible) {
            return;
        }

        Entity entity = NpcEntityAccess.getBukkitEntity(npc);
        if (entity != null) {
            entity.setInvisible(false);
        }
    }

    private static final class AppliedModel {
        private final Npc npc;
        private final String modelName;
        private final Dummy<Npc> dummy;
        private final ModeledEntity modeledEntity;
        private final ActiveModel activeModel;
        private volatile Location lastSyncedLocation;

        private AppliedModel(Npc npc, String modelName, Dummy<Npc> dummy, ModeledEntity modeledEntity, ActiveModel activeModel) {
            this.npc = npc;
            this.modelName = modelName;
            this.dummy = dummy;
            this.modeledEntity = modeledEntity;
            this.activeModel = activeModel;
        }
    }
}
