package com.fancyinnovations.fancydialogs.api.click;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ClickAction(
        @NotNull Button button,
        @Nullable ClickEvent onClick
) {
}
