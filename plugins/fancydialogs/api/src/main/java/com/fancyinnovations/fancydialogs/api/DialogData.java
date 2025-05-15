package com.fancyinnovations.fancydialogs.api;

import com.fancyinnovations.fancydialogs.api.body.DialogBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record DialogData(
        @NotNull String id,
        @NotNull String title,
        @Nullable String externalTitle,
        boolean canCloseWithEscape,
        @NotNull List<DialogBody> body
) {

}
