package com.fancyinnovations.fancynpcsmodel.providers.bettermodel;

import com.fancyinnovations.fancynpcsmodel.main.FancyNpcsModelPlugin;
import com.fancyinnovations.fancynpcsmodel.providers.ModelProvider;
import com.fancyinnovations.fancynpcsmodel.utils.NpcEntityAccess;
import de.oliver.fancyanalytics.logger.properties.StringProperty;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.animation.AnimationModifier;
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter;
import kr.toxicity.model.api.event.hitbox.HitBoxDamagedEvent;
import kr.toxicity.model.api.event.hitbox.HitBoxInteractAtEvent;
import kr.toxicity.model.api.platform.PlatformEntity;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.EntityTrackerRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Model provider backed by the BetterModel plugin.
 */
public class BetterModelProvider implements ModelProvider {

    @Override
    public String getId() {
        return "bettermodel";
    }

    @Override
    public String getDisplayName() {
        return "BetterModel";
    }

    @Override
    public Collection<String> getPrefixes() {
        return List.of("bm", "bettermodel");
    }

    @Override
    public Collection<String> getModelNames() {
        return BetterModel.modelKeys().stream().toList();
    }

    @Override
    public boolean hasModel(String modelName) {
        return BetterModel.model(modelName).isPresent();
    }

    @Override
    public void applyModel(Npc npc, String modelName) {
        Entity bukkitEntity = NpcEntityAccess.getBukkitEntity(npc);
        if (bukkitEntity == null) {
            return;
        }
        bukkitEntity.customName(Component.empty());

        // Close all existing trackers
        closeAllTrackers(bukkitEntity);

        // Gets or creates entity tracker
        EntityTracker tracker = BetterModel.model(modelName)
                .map(r -> r.getOrCreate(BukkitAdapter.adapt(bukkitEntity)))
                .orElse(null);
        if (tracker == null) {
            FancyNpcsModelPlugin.get().getFancyLogger().error(
                    "Failed to get model with name " + modelName,
                    StringProperty.of("model_name", modelName),
                    StringProperty.of("npc_name", npc.getData().getName())
            );
            return;
        }

        // Scale
        if (npc.getData().getScale() != 1) {
            tracker.scaler(tracker.scaler().multiply(npc.getData().getScale()));
        }

        // Right click on hitbox
        tracker.listenHitBox(HitBoxInteractAtEvent.class, event -> {
            Player player = Bukkit.getPlayer(event.getWho().uuid());
            if (player == null) return;

            npc.interact(player, ActionTrigger.RIGHT_CLICK);
        });

        // Left click on hitbox
        tracker.listenHitBox(HitBoxDamagedEvent.class, event -> {
            PlatformEntity causingEntity = event.getSource().getCausingEntity();
            if (causingEntity == null) return;
            Player player = Bukkit.getPlayer(causingEntity.uuid());
            if (player == null) return;

            npc.interact(player, ActionTrigger.LEFT_CLICK);
        });

        EntityTrackerRegistry registry = tracker.registry();
        for (Player player : Bukkit.getOnlinePlayers()) {
            registry.spawn(BukkitAdapter.adapt(player));
        }
    }

    @Override
    public void removeModel(Npc npc) {
        Entity bukkitEntity = NpcEntityAccess.getBukkitEntity(npc);
        if (bukkitEntity == null) {
            return;
        }

        closeAllTrackers(bukkitEntity);
    }

    @Override
    public boolean hasModelApplied(Npc npc) {
        return getEntityTracker(npc) != null;
    }

    @Override
    public boolean playAnimation(Npc npc, String animation, boolean loop) {
        EntityTracker tracker = getEntityTracker(npc);
        if (tracker == null) {
            return false;
        }

        AnimationModifier modifier = loop ? AnimationModifier.DEFAULT : AnimationModifier.DEFAULT_WITH_PLAY_ONCE;
        return tracker.animate(animation, modifier);
    }

    @Override
    public Collection<String> getAnimationNames(Npc npc) {
        EntityTracker tracker = getEntityTracker(npc);
        if (tracker == null) {
            return List.of();
        }

        return tracker.renderer().animations().keySet().stream().toList();
    }

    @Override
    public boolean shouldCancelNativeInteraction() {
        // clicks are routed through BetterModel's hitbox events
        return true;
    }

    /**
     * Closes all model trackers for the given NPC's entity.
     * This is necessary to prevent old trackers still existing in the world.
     */
    private static void closeAllTrackers(Entity bukkitEntity) {
        BetterModel.registry(BukkitAdapter.adapt(bukkitEntity)).ifPresent(reg -> {
            for (EntityTracker tracker : reg.trackers()) {
                tracker.close();
            }
        });
    }

    private static @Nullable EntityTracker getEntityTracker(Npc npc) {
        Entity bukkitEntity = NpcEntityAccess.getBukkitEntity(npc);
        if (bukkitEntity == null) {
            return null;
        }

        Optional<EntityTrackerRegistry> trackersOpt = BetterModel.registry(BukkitAdapter.adapt(bukkitEntity));
        if (trackersOpt.isEmpty()) return null;

        Collection<EntityTracker> trackers = trackersOpt.get().trackers();
        if (trackers.isEmpty()) return null;

        return trackers.iterator().next();
    }
}
