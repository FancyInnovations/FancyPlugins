package com.fancyinnovations.fancydialogs.api.types;

import com.fancyinnovations.fancydialogs.api.DialogData;
import com.fancyinnovations.fancydialogs.api.click.ClickAction;
import com.fancyinnovations.fancydialogs.api.click.ClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record MultiActionDialog(
        @NotNull DialogData common,
        @NotNull List<ClickAction> actions,
        @Nullable ClickEvent onCancel,
        int columns
) implements Dialog{

}
