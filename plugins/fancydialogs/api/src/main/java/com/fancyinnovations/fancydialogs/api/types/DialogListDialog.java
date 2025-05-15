package com.fancyinnovations.fancydialogs.api.types;

import com.fancyinnovations.fancydialogs.api.DialogData;
import com.fancyinnovations.fancydialogs.api.click.ClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record DialogListDialog(
        @NotNull DialogData common,
        @NotNull List<Dialog> dialogs,
        @Nullable ClickEvent onCancel,
        int columns,
        int buttonWidth
) implements Dialog {
}
