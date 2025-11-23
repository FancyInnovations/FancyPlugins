package com.fancyinnovations.fancydialogs.api.data;

public record DialogBodyData(
        String text,
        Integer width // nullable, optional per entry
) {
}
