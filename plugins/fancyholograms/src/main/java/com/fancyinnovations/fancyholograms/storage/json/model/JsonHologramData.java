package com.fancyinnovations.fancyholograms.storage.json.model;

import com.fancyinnovations.fancyholograms.api.data.property.Visibility;
import com.fancyinnovations.fancyholograms.api.hologram.HologramType;

import java.util.List;

public record JsonHologramData(
        String name,
        HologramType type,
        JsonLocation location,
        String worldName,
        Integer visibilityDistance,
        Visibility visibility,
        String linkedNpcName,
        Float yOffset,
        List<String> traits
) {
}

