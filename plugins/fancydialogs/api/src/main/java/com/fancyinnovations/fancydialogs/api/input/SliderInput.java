package com.fancyinnovations.fancydialogs.api.input;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record SliderInput(
        int width,
        @NotNull String label,
        @NotNull String labelFormat,
        @NotNull RangeInfo rangeInfo
) implements DialogInput {

    public record RangeInfo(
            double start,
            double end,
            @Nullable Double initial,
            int steps
    ) {

    }

}
