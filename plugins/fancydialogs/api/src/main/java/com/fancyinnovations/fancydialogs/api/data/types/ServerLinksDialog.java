package com.fancyinnovations.fancydialogs.api.data.types;

import com.fancyinnovations.fancydialogs.api.data.DialogData;
import com.fancyinnovations.fancydialogs.api.data.click.ClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ServerLinksDialog(
        @NotNull DialogData common,
        @Nullable ClickEvent onCancel,
        int columns,
        int buttonWidth
) implements DialogType {
}
