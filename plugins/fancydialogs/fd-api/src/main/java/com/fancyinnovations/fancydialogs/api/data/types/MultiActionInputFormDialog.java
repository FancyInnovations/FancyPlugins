package com.fancyinnovations.fancydialogs.api.data.types;

import com.fancyinnovations.fancydialogs.api.data.DialogData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record MultiActionInputFormDialog(
        @NotNull DialogData common,
        @NotNull List<InputFormDialog.Input> inputs,
        @NotNull List<InputFormDialog.SubmitAction> actions
) implements DialogType {

}
