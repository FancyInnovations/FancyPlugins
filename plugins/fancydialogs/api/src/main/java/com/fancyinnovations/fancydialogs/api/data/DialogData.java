package com.fancyinnovations.fancydialogs.api.data;

import com.fancyinnovations.fancydialogs.api.data.body.DialogBody;
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
