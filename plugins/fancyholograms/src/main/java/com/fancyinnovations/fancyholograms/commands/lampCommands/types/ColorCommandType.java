package com.fancyinnovations.fancyholograms.commands.lampCommands.types;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.BukkitExceptionHandler;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

import java.util.ArrayList;
import java.util.List;

public class ColorCommandType extends BukkitExceptionHandler implements ParameterType<BukkitCommandActor, Color> {

    public static final ColorCommandType INSTANCE = new ColorCommandType();

    public static final String TRANSPARENT_SUGGESTION = "@transparent";
    public static final Color TRANSPARENT_COLOR = Color.fromARGB(0);
    public static final String DEFAULT_SUGGESTION = "@default";

    private ColorCommandType() {
    }

    public static String toString(Color color) {
        if (color == null) {
            return DEFAULT_SUGGESTION;
        }

        if (color.asARGB() == TRANSPARENT_COLOR.asARGB()) {
            return TRANSPARENT_SUGGESTION;
        }

        for (NamedTextColor named : NamedTextColor.NAMES.values()) {
            if (color.equals(Color.fromARGB(named.value() | 0xC8000000))) {
                return named.toString().toLowerCase();
            }
        }
        return String.format("#%08X", color.asARGB());
    }

    @Override
    public Color parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<@NotNull BukkitCommandActor> context) {
        String inputStr = input.readString();

        if (inputStr.equals(DEFAULT_SUGGESTION)) {
            return null;
        }

        if (inputStr.equals(TRANSPARENT_SUGGESTION)) {
            return TRANSPARENT_COLOR;
        }

        Color color;

        if (inputStr.startsWith("#")) {
            Color parsed = Color.fromARGB((int) Long.parseLong(inputStr.substring(1), 16));

            // make color solid inputStr if RGB hex provided
            if (inputStr.length() == 7) {
                color = parsed.setAlpha(255);
            } else {
                color = parsed;
            }
        } else {
            NamedTextColor named = NamedTextColor.NAMES.value(inputStr.replace(' ', '_'));
            color = named == null ? null : Color.fromARGB(named.value() | 0xC8000000);
        }

        if (color == null) {
            throw new InvalidColorException(inputStr);
        }

        return color;
    }

    @Override
    public @NotNull SuggestionProvider<@NotNull BukkitCommandActor> defaultSuggestions() {
        return (ctx) -> {
            List<String> suggestions = new ArrayList<>();
            for (NamedTextColor color : NamedTextColor.NAMES.values()) {
                suggestions.add(color.toString().toLowerCase());
            }
            suggestions.add(DEFAULT_SUGGESTION);
            suggestions.add(TRANSPARENT_SUGGESTION);
            return suggestions;
        };
    }

    public static class InvalidColorException extends IllegalArgumentException {
        public InvalidColorException(@NotNull String input) {
            super(input);
        }
    }
}
