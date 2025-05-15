package com.fancyinnovations.fancydialogs.api.types;

import com.fancyinnovations.fancydialogs.api.DialogData;
import com.fancyinnovations.fancydialogs.api.click.ClickAction;
import org.jetbrains.annotations.NotNull;

public record ConfirmationDialog(
        @NotNull DialogData common,
        @NotNull ClickAction yesButton,
        @NotNull ClickAction noButton
) implements Dialog{

}
