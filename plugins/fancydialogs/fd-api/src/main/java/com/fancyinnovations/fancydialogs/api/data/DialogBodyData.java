package com.fancyinnovations.fancydialogs.api.data;
import org.jetbrains.annotations.Nullable;

public record DialogBodyData(
        String text,
        @Nullable Integer width
) {
    public DialogBodyData(String text) {
        this(text, null);
    }
}
