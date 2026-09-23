package de.oliver.fancynpcs.commands.lampCommands.types;

import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public final class GreedyStringCommandType implements ParameterType<BukkitCommandActor, String> {

    public static final GreedyStringCommandType INSTANCE = new GreedyStringCommandType();

    public GreedyStringCommandType() {
    }

    @Override
    public String parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<BukkitCommandActor> context) {
        return input.consumeRemaining();
    }

    @Override
    public boolean isGreedy() {
        return true;
    }
}
