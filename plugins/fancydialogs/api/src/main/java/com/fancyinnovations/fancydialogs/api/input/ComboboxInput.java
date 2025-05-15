package com.fancyinnovations.fancydialogs.api.input;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record ComboboxInput (
        int width,
        @NotNull List<Entry> entries,
        @NotNull String label,
        boolean labelVisible
) implements DialogInput {

    public record Entry(
            @NotNull String id,
            @Nullable String display,
            boolean initial
    ) {

    }
}
