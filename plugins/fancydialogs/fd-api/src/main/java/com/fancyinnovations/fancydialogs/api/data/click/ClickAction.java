package com.fancyinnovations.fancydialogs.api.data.click;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ClickAction(
        @NotNull Button button,
        @Nullable ClickEvent onClick
) {
}
