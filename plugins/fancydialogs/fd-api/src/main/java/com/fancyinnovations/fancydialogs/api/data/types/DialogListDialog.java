package com.fancyinnovations.fancydialogs.api.data.types;

import com.fancyinnovations.fancydialogs.api.data.DialogData;
import com.fancyinnovations.fancydialogs.api.data.click.ClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record DialogListDialog(
        @NotNull DialogData common,
        @NotNull List<DialogType> dialogs,
        @Nullable ClickEvent exitAction,
        int columns,
        int buttonWidth
) implements DialogType {
}
