package com.fancyinnovations.fancyholograms.storage.json.model;

import org.bukkit.entity.TextDisplay;

import java.util.List;

public record JsonTextHologramData(
        List<String> text,
        Boolean text_shadow,
        Boolean see_through,
        TextDisplay.TextAlignment text_alignment,
        Integer text_update_interval,
        Integer text_opacity,
        String background_color
) {
}
