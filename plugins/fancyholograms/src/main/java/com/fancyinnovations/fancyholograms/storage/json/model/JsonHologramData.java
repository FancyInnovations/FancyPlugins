package com.fancyinnovations.fancyholograms.storage.json.model;

import com.fancyinnovations.fancyholograms.api.data.property.Visibility;
import com.fancyinnovations.fancyholograms.api.hologram.HologramType;

import java.util.List;

public record JsonHologramData(
        String name,
        HologramType type,
        JsonLocation location,
        String world_name,
        Integer visibility_distance,
        Visibility visibility,
        String linked_npc_name,
        List<String> traits
) {
}

