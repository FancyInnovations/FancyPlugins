package com.fancyinnovations.fancydialogs.api.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record DialogData(
        @NotNull String id,
        @NotNull String title,
        @Nullable String externalTitle,
        boolean canCloseWithEscape,
        @NotNull List<DialogBodyData> body,
        @NotNull List<DialogButton> buttons
) {

}
