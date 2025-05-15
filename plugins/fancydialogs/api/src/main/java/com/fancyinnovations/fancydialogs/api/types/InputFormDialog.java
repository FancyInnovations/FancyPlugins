package com.fancyinnovations.fancydialogs.api.types;

import com.fancyinnovations.fancydialogs.api.click.Button;
import com.fancyinnovations.fancydialogs.api.input.DialogInput;
import com.fancyinnovations.fancydialogs.api.submit.SubmitMethod;
import org.jetbrains.annotations.NotNull;

public interface InputFormDialog extends Dialog {

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
