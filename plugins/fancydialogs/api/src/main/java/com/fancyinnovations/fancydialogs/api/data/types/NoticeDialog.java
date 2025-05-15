package com.fancyinnovations.fancydialogs.api.data.types;

import com.fancyinnovations.fancydialogs.api.data.DialogData;
import com.fancyinnovations.fancydialogs.api.data.click.ClickAction;
import org.jetbrains.annotations.NotNull;

public record NoticeDialog(
        @NotNull DialogData common,
        @NotNull ClickAction button
) implements Dialog {
}
