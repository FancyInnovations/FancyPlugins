package com.fancyinnovations.fancydialogs.api.types;

import com.fancyinnovations.fancydialogs.api.DialogData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SimpleInputFormDialog(
        @NotNull DialogData common,
        @NotNull List<InputFormDialog.Input> inputs,
        @NotNull InputFormDialog.SubmitAction action
) implements Dialog{

}
