package com.fancyinnovations.fancyholograms.commands.lampCommands.types;

import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.colors.GlowingColor;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.BukkitExceptionHandler;
import revxrsal.commands.exception.InvalidValueException;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

import java.util.Arrays;

public class GlowingColorCommandType extends BukkitExceptionHandler implements ParameterType<BukkitCommandActor, GlowingColor> {

    public static final GlowingColorCommandType INSTANCE = new GlowingColorCommandType();

    private GlowingColorCommandType() {
    }

    @Override
    public GlowingColor parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<@NotNull BukkitCommandActor> context) {
        String colorName = input.readString();
        try {
            return GlowingColor.valueOf(colorName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidGlowingColorException(colorName);
        }
    }

    @HandleException
    public void onInvalidColor(InvalidGlowingColorException e, BukkitCommandActor actor) {
        FancyHologramsPlugin.get().getTranslator()
                .translate("commands.hologram.edit.glowing.invalid_color")
                .replace("color", e.input())
                .send(actor.sender());
    }

    @Override
    public @NotNull SuggestionProvider<@NotNull BukkitCommandActor> defaultSuggestions() {
        return (ctx) -> Arrays.stream(GlowingColor.values())
                .map(color -> color.name().toLowerCase())
                .toList();
    }

    public static class InvalidGlowingColorException extends InvalidValueException {
        public InvalidGlowingColorException(@NotNull String input) {
            super(input);
        }
    }

}
