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
        Float displayNameScale,
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
        @Deprecated
        Float interactionCooldown, // Deprecated: use triggerCooldowns
        Map<String, Float> triggerCooldowns, // Cooldown per trigger (ANY_CLICK, LEFT_CLICK, RIGHT_CLICK)
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
            String variant,
            // Resource pack texture skin fields (1.21.11+ Mannequin)
            Boolean texturePackSkin,
            String capeTextureAsset,
            String elytraTextureAsset
    ) {
        // Constructor for backwards compatibility
        public JsonSkin(String identifier, String variant) {
            this(identifier, variant, null, null, null);
        }
    }

    public record JsonAction(
            Integer order,
            String action,
            String value
    ) {
    }
}
