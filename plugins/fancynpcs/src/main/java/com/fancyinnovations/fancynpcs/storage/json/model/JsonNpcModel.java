package com.fancyinnovations.fancynpcs.storage.json.model;

import com.fancyinnovations.fancynpcs.api.data.property.NpcVisibility;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.Map;

public record JsonNpcModel(
        String id,
        String name,
        String creator,
        String displayName,
        EntityType type,
        JsonLocation location,
        JsonSkin skin,
        Boolean mirrorSkin,
        Boolean showInTab,
        Boolean spawnEntity,
        Boolean collidable,
        Boolean glowing,
        String glowingColor,
        Boolean turnToPlayer,
        Integer turnToPlayerDistance,
        Float interactionCooldown,
        Float scale,
        Integer visibilityDistance,
        NpcVisibility visibility,
        Map<String, String> equipment,
        Map<String, String> attributes,
        Map<String, List<JsonAction>> actions
) {

    public record JsonLocation(
            String world,
            Double x,
            Double y,
            Double z,
            Float yaw,
            Float pitch
    ) {
    }

    public record JsonSkin(
            String identifier,
            String variant
    ) {
    }

    public record JsonAction(
            Integer order,
            String action,
            String value
    ) {
    }
}
