package com.fancyinnovations.fancynpcsmodel.utils;

import com.fancyinnovations.fancynpcsmodel.main.FancyNpcsModelPlugin;
import de.oliver.fancyanalytics.logger.properties.StringProperty;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.fancylib.ReflectionUtils;
import de.oliver.fancynpcs.api.Npc;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

/**
 * Provides access to the (packet based) entity behind a FancyNpcs NPC.
 */
public final class NpcEntityAccess {

    private NpcEntityAccess() {
    }

    /**
     * @return the bukkit entity behind the given NPC or null if it could not be resolved
     */
    public static @Nullable Entity getBukkitEntity(Npc npc) {
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
}
