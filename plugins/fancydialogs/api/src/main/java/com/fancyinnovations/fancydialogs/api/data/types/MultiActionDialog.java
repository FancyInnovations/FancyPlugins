package com.fancyinnovations.fancydialogs.api.data.types;

import com.fancyinnovations.fancydialogs.api.data.DialogData;
import com.fancyinnovations.fancydialogs.api.data.click.ClickAction;
import com.fancyinnovations.fancydialogs.api.data.click.ClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record MultiActionDialog(
        @NotNull DialogData common,
        @NotNull List<ClickAction> actions,
        @Nullable ClickEvent onCancel,
        int columns
) implements DialogType {

}
