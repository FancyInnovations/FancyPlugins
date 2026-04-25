package com.fancyinnovations.fancyholograms.commands.lampCommands.types;

import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.colors.GlowingColor;
import de.oliver.fancylib.duration.FancyDuration;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.BukkitExceptionHandler;
import revxrsal.commands.exception.InvalidValueException;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

import java.util.Arrays;
import java.util.List;

public class DurationCommandType extends BukkitExceptionHandler implements ParameterType<BukkitCommandActor, FancyDuration> {

    public static final DurationCommandType INSTANCE = new DurationCommandType();

    private DurationCommandType() {
    }

    @Override
    public FancyDuration parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<@NotNull BukkitCommandActor> context) {
        String duration = input.readString();
        try {
            return FancyDuration.parse(duration);
        } catch (IllegalArgumentException e) {
            throw new InvalidTimeException(duration);
        }
    }

    @HandleException
    public void onInvalidTime(InvalidTimeException e, BukkitCommandActor actor) {
        FancyHologramsPlugin.get().getTranslator()
                .translate("common.invalid_duration")
                .withPrefix()
                .replace("duration", e.input())
                .send(actor.sender());
    }

    @Override
    public @NotNull SuggestionProvider<@NotNull BukkitCommandActor> defaultSuggestions() {
        return (ctx) -> List.of("off", "1s", "5s", "10s", "30s", "1m", "5m", "10m", "30m", "1h", "6h", "12h", "1d");
    }

    public static class InvalidTimeException extends InvalidValueException {
        public InvalidTimeException(@NotNull String input) {
            super(input);
        }
    }

}
