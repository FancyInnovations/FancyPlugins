package de.oliver.fancynpcs.commands.lampCommands.types;

import de.oliver.fancynpcs.api.utils.Interval;
import de.oliver.fancynpcs.api.utils.Interval.Unit;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

import java.util.regex.Pattern;

public final class IntervalCommandType implements ParameterType<BukkitCommandActor, Interval> {

    public static final IntervalCommandType INSTANCE = new IntervalCommandType();
    private static final Pattern SPLIT = Pattern.compile("(?<=\\d)(?=\\D)");

    private IntervalCommandType() {
    }

    @Override
    public Interval parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<BukkitCommandActor> context) {
        String value = input.readString();
        if (value.equalsIgnoreCase("disabled")) {
            return Interval.of(0, Unit.MILLISECONDS);
        }

        String[] split = SPLIT.split(value);
        if (split.length != 2) {
            throw new FancyNpcsParameterException("command_invalid_interval", value);
        }

        try {
            Unit unit = Unit.fromShortCode(split[1].toLowerCase());
            return Interval.of(Math.max(0, Long.parseLong(split[0])), unit);
        } catch (RuntimeException e) {
            throw new FancyNpcsParameterException("command_invalid_interval", value);
        }
    }
}
