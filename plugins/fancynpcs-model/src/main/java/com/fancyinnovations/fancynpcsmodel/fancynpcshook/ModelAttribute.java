package com.fancyinnovations.fancynpcsmodel.fancynpcshook;

import com.fancyinnovations.fancynpcsmodel.main.FancyNpcsModelPlugin;
import de.oliver.fancyanalytics.logger.properties.StringProperty;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.fancylib.ReflectionUtils;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

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

    private static void setModel(Npc npc, String model) {
        Object nmsEntity = ReflectionUtils.getValue(npc, "npc");
        if (nmsEntity == null) {
            // TODO: create fake nms / bukkit entity object once FancyNpcs itself doesn't store the entity object anymore (when migrated to FancySitula)
            FancyNpcsModelPlugin.get().getFancyLogger().error("Failed to get NMS entity from NPC");
            return;
        }

        Entity bukkitEntity;
        try {
            bukkitEntity = (Entity) ReflectionUtils.getMethod(nmsEntity, "getBukkitEntity").invoke(nmsEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            FancyNpcsModelPlugin.get().getFancyLogger().error(
                    "Failed to invoke getBukkitEntity method on NMS entity",
                    ThrowableProperty.of(e),
                    StringProperty.of("npc_name", npc.getData().getName())
            );
            return;
        }


        System.out.println("BUKKIT ENTITY: " + bukkitEntity.getEntityId());
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
