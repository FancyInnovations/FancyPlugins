package de.oliver.fancynpcs.commands.lampCommands.types;

import org.jetbrains.annotations.NotNull;
import revxrsal.commands.exception.InvalidValueException;

public final class FancyNpcsParameterException extends InvalidValueException {

    private final String translationKey;

    public FancyNpcsParameterException(@NotNull String translationKey, @NotNull String input) {
        super(input);
        this.translationKey = translationKey;
    }

    public FancyNpcsParameterException(@NotNull String input) {
        this("command_invalid_value", input);
    }

    public String translationKey() {
        return translationKey;
    }
}
