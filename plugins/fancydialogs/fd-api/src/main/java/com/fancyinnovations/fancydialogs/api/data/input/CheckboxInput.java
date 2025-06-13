package com.fancyinnovations.fancydialogs.api.data.input;

import org.jetbrains.annotations.NotNull;

public record CheckboxInput(
        @NotNull String label,
        boolean initial,
        @NotNull String onTrue,
        @NotNull String onFalse
) implements DialogInput {

}
