package com.fancyinnovations.fancynpcsmodel.fancynpcshook;

import com.fancyinnovations.fancynpcsmodel.main.FancyNpcsModelPlugin;
import de.oliver.fancyanalytics.logger.properties.StringProperty;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.fancylib.ReflectionUtils;
import de.oliver.fancylib.serverSoftware.ServerSoftware;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter;
import kr.toxicity.model.api.event.hitbox.HitBoxDamagedEvent;
import kr.toxicity.model.api.event.hitbox.HitBoxInteractAtEvent;
import kr.toxicity.model.api.platform.PlatformEntity;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.EntityTrackerRegistry;
import kr.toxicity.model.api.tracker.ModelScaler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CustomModelAttribute {

    public static final String ATTRIBUTE_NAME = "custom_model";

    public static NpcAttribute getModelAttribute() {
        return new NpcAttribute(
                ATTRIBUTE_NAME,
                () -> BetterModel.modelKeys().stream().toList(),
                List.of(EntityType.PLAYER),
                CustomModelAttribute::setModel
        );
    }

    private static void setModel(Npc npc, String modelName) {
        Entity bukkitEntity = getBukkitEntity(npc);
        if (bukkitEntity == null) {
            return;
        }
        bukkitEntity.customName(Component.empty());

        // Close all existing trackers
        closeAllTrackers(bukkitEntity);

        // remove model if model name is "@none"
        if (modelName.equalsIgnoreCase("@none")) {
            return;
        }

        // FancyNpcs applies attributes (this) before it applies the npc's configured
        // rotation to its underlying fake entity (Npc#update calls
        // NpcData#applyAllAttributes before Npc#move), so the entity's live yaw/pitch
        // can still be stale/default at this point. BetterModel reads the entity's
        // current rotation when the tracker is created, so without this the model can
        // spawn facing the wrong way until something else (e.g. /bettermodel reload)
        // forces a fresh read after the entity's rotation is actually correct.
        if (npc.getData().getLocation() != null) {
            bukkitEntity.setRotation(npc.getData().getLocation().getYaw(), npc.getData().getLocation().getPitch());
        }

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

        configureTracker(npc, tracker);

        EntityTrackerRegistry registry = tracker.registry();
        for (Player player : Bukkit.getOnlinePlayers()) {
            runOnPlayerScheduler(player, () -> registry.spawn(BukkitAdapter.adapt(player)));

            // Safety net: on server restart / world change, the model has occasionally been
            // observed to silently not show up for a player on the first attempt (no error
            // logged - BetterModel itself seemingly not fully ready yet), only recovering after
            // a full extra respawn cycle (e.g. re-joining or changing world again). Retrying the
            // spawn shortly after is harmless (spawn() is safe to call again on an already-shown
            // model) and self-heals that case without requiring the player to do anything.
            runOnPlayerSchedulerDelayed(player, () -> registry.spawn(BukkitAdapter.adapt(player)), 40L);
        }
    }

    /**
     * Applies the scale and (re-)registers the hitbox click listeners on a tracker.
     * Called both right after creating a tracker in {@link #setModel} and from
     * {@link #onTrackerCreated}, since BetterModel discards and recreates trackers
     * (e.g. on {@code /bettermodel reload}), silently dropping any listeners that
     * were only registered once at model-set time.
     */
    private static void configureTracker(Npc npc, EntityTracker tracker) {
        // Scale
        // Uses an absolute value, not tracker.scaler().multiply(...): configureTracker() runs
        // again on every respawn (setModel() re-runs on every update() cycle), and multiplying
        // the tracker's current scaler compounds on every single call - the model grows
        // exponentially the longer the server runs, eventually causing multi-second hitbox
        // collision-check hangs (BlockGetter#forEachBlockIntersectedBetween) as the model size
        // explodes.
        if (npc.getData().getScale() != 1) {
            tracker.scaler(ModelScaler.value(npc.getData().getScale()));
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
    }

    /**
     * Called whenever BetterModel (re-)creates an EntityTracker (e.g. on {@code /bettermodel reload}).
     * Re-attaches this plugin's hitbox listeners if the tracker belongs to one of our custom-model npcs.
     */
    public static void onTrackerCreated(EntityTracker tracker) {
        UUID entityUuid = tracker.registry().entity().uuid();

        for (Npc npc : FancyNpcsPlugin.get().getNpcManager().getAllNpcs()) {
            if (!hasAttribute(npc)) {
                continue;
            }

            Entity bukkitEntity = getBukkitEntity(npc);
            if (bukkitEntity == null || !bukkitEntity.getUniqueId().equals(entityUuid)) {
                continue;
            }

            configureTracker(npc, tracker);
            return;
        }
    }

    private static Entity getBukkitEntity(Npc npc) {
        // get the nms entity object from the Npc implementation classes
        Object nmsEntity = ReflectionUtils.getValue(npc, "npc");
        if (nmsEntity == null) {
            // TODO: create fake nms / bukkit entity object once FancyNpcs itself doesn't store the entity object anymore (when migrated to FancySitula)
            FancyNpcsModelPlugin.get().getFancyLogger().error("Failed to get NMS entity from NPC");
            return null;
        }

        // call the Entity#getBukkitEntity method to get the bukkit entity object
        try {
            return (Entity) ReflectionUtils.getMethod(nmsEntity, "getBukkitEntity").invoke(nmsEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            FancyNpcsModelPlugin.get().getFancyLogger().error(
                    "Failed to invoke getBukkitEntity method on NMS entity",
                    ThrowableProperty.of(e),
                    StringProperty.of("npc_name", npc.getData().getName())
            );
            return null;
        }
    }

    /**
     * Closes all model trackers for the given NPC's entity.
     * This is necessary to prevent old trackers still existing in the world.
     */
    public static void closeAllTrackers(Npc npc) {
        Entity bukkitEntity = getBukkitEntity(npc);
        if (bukkitEntity == null) {
            return;
        }

        closeAllTrackers(bukkitEntity);
    }

    private static void closeAllTrackers(Entity bukkitEntity) {
        BetterModel.registry(BukkitAdapter.adapt(bukkitEntity)).ifPresent(reg -> {
            for (EntityTracker tracker : reg.trackers()) {
                tracker.close();
            }
        });
    }

    /**
     * @return whether the given NPC has the model attribute
     */
    public static boolean hasAttribute(Npc npc) {
        for (Map.Entry<NpcAttribute, String> entry : npc.getData().getAttributes().entrySet()) {
            if (entry.getKey().getName().equalsIgnoreCase(ATTRIBUTE_NAME)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Runs a task on the player's own region scheduler when using Folia.
     * BetterModel's tracker API touches per-region state (e.g. scheduling hitbox
     * packets for the player's location) and must be called from the thread that
     * owns that region, or it fails (NullPointerException on RegionizedData) - the
     * same reason FancyNpcs itself always sends its packets through this scheduler.
     */
    public static void runOnPlayerScheduler(Player player, Runnable task) {
        if (ServerSoftware.isFolia()) {
            player.getScheduler().run(FancyNpcsModelPlugin.get(), (t) -> task.run(), null);
            return;
        }

        task.run();
    }

    /**
     * Same as {@link #runOnPlayerScheduler}, delayed by the given amount of ticks.
     */
    private static void runOnPlayerSchedulerDelayed(Player player, Runnable task, long delayTicks) {
        if (ServerSoftware.isFolia()) {
            player.getScheduler().runDelayed(FancyNpcsModelPlugin.get(), (t) -> task.run(), null, delayTicks);
            return;
        }

        Bukkit.getScheduler().runTaskLater(FancyNpcsModelPlugin.get(), task, delayTicks);
    }

    public static EntityTracker getEntityTracker(Npc npc) {
        Entity bukkitEntity = getBukkitEntity(npc);
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
