package com.fancyinnovations.fancydialogs.api.data.types;

import com.fancyinnovations.fancydialogs.api.data.click.Button;
import com.fancyinnovations.fancydialogs.api.data.input.DialogInput;
import com.fancyinnovations.fancydialogs.api.data.submit.SubmitMethod;
import org.jetbrains.annotations.NotNull;

public interface InputFormDialog extends DialogType {

   record Input(
           @NotNull String key,
           @NotNull DialogInput control
   ) {
   }

   record SubmitAction(
           @NotNull String id,
           @NotNull Button buttonData,
           @NotNull SubmitMethod method
   ) {
   }
}
