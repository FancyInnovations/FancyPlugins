package com.fancyinnovations.fancydialogs.api.data;

import com.fancyinnovations.fancydialogs.api.data.body.DialogBody;
import com.fancyinnovations.fancydialogs.api.data.input.DialogInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record DialogData(
        @NotNull String title,
        @Nullable String externalTitle,
        boolean canCloseWithEscape,
        boolean pause, // only relevant in single player
        @NotNull DialogAction afterAction,
        @NotNull List<DialogBody> body,
        @NotNull List<DialogInput> inputs
) {

}
