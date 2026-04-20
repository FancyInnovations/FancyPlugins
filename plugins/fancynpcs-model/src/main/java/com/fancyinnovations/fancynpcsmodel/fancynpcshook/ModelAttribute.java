package com.fancyinnovations.fancynpcsmodel.fancynpcshook;

import com.fancyinnovations.fancynpcsmodel.main.FancyNpcsModelPlugin;
import de.oliver.fancyanalytics.logger.properties.StringProperty;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.fancylib.ReflectionUtils;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter;
import kr.toxicity.model.api.bukkit.platform.BukkitPlayer;
import kr.toxicity.model.api.data.renderer.ModelRenderer;
import kr.toxicity.model.api.tracker.EntityHideOption;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.EntityTrackerRegistry;
import kr.toxicity.model.api.tracker.TrackerModifier;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ModelAttribute {

    private static final String ATTRIBUTE_NAME = "custom_model";

    public static NpcAttribute getModelAttribute() {
        return new NpcAttribute(
                ATTRIBUTE_NAME,
                List.of(),
                List.of(EntityType.PLAYER),
                ModelAttribute::setModel
        );
    }

    private static void setModel(Npc npc, String modelName) {
        Entity bukkitEntity = getBukkitEntity(npc);
        if (bukkitEntity == null) {
            return;
        }
        bukkitEntity.customName(Component.empty());

        // Close all existing trackers
        BetterModel.registry(BukkitAdapter.adapt(bukkitEntity)).ifPresent(reg -> {
            for (EntityTracker tracker : reg.trackers()) {
                tracker.close();
            }
        });

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

        EntityTrackerRegistry registry = tracker.registry();
        for (Player player : Bukkit.getOnlinePlayers()) {
            registry.spawn(BukkitAdapter.adapt(player));
        }
    }

    private static Entity getBukkitEntity(Npc npc) {
        Object nmsEntity = ReflectionUtils.getValue(npc, "npc");
        if (nmsEntity == null) {
            // TODO: create fake nms / bukkit entity object once FancyNpcs itself doesn't store the entity object anymore (when migrated to FancySitula)
            FancyNpcsModelPlugin.get().getFancyLogger().error("Failed to get NMS entity from NPC");
            return null;
        }

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
     * @return The model name of the given NPC, or null if it doesn't have one
     */
    public static String getModelFromNpc(Npc npc) {
        for (Map.Entry<NpcAttribute, String> entry : npc.getData().getAttributes().entrySet()) {
            if (entry.getKey().getName().equalsIgnoreCase(ATTRIBUTE_NAME)) {
                return entry.getValue();
            }
        }

        return null;
    }
}
