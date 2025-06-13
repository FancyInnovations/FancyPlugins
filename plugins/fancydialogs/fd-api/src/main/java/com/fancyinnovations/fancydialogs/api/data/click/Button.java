package com.fancyinnovations.fancydialogs.api.data.click;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Button(
        @NotNull String label,
        @Nullable String tooltip,
        int width
) {
}
