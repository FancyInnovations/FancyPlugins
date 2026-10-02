package de.oliver.fancynpcs.commands.lampCommands.types;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.parameters.LocationParameterType;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public final class LocationCommandType implements ParameterType<BukkitCommandActor, Location> {

    public static final LocationCommandType INSTANCE = new LocationCommandType();
    private final LocationParameterType delegate = new LocationParameterType();

    private LocationCommandType() {
    }

    @Override
    public Location parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<BukkitCommandActor> context) {
        return delegate.parse(input, context);
    }
}
