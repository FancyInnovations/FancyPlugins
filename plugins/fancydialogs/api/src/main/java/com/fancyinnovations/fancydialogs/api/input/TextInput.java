package com.fancyinnovations.fancydialogs.api.input;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record TextInput(
        int width,
        @NotNull String label,
        boolean labelVisible,
        @Nullable String initial
) implements DialogInput {

}
