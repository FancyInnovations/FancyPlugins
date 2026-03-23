package com.fancyinnovations.fancyworlds.commands.types;

import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.api.worlds.WorldService;
import com.fancyinnovations.fancyworlds.main.FancyWorldsPlugin;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.BukkitExceptionHandler;
import revxrsal.commands.exception.InvalidValueException;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public class FWorldCommandType extends BukkitExceptionHandler implements ParameterType<BukkitCommandActor, FWorld> {

    public static final FWorldCommandType INSTANCE = new FWorldCommandType();
    private static final WorldService SERVICE = WorldService.get();

    private FWorldCommandType() {
        // Private constructor to prevent instantiation
    }

    @Override
    public FWorld parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<@NotNull BukkitCommandActor> context) {
        String worldName = input.readString();

        FWorld fworld = SERVICE.getWorldByName(worldName);
        if (fworld != null) {
            return fworld;
        }

        throw new InvalidFWorldException(worldName);
    }

    @HandleException
    public void onInvalidFWorld(InvalidFWorldException e, BukkitCommandActor actor) {
        FancyWorldsPlugin.get().getTranslator()
                .translate("common.world_not_found")
                .withPrefix()
                .replace("worldName", e.input())
                .send(actor.sender());
    }

    @Override
    public @NotNull SuggestionProvider<@NotNull BukkitCommandActor> defaultSuggestions() {
        return (ctx) -> SERVICE.getAllWorlds().stream()
                .map(FWorld::getName)
                .toList();
    }

    public static class InvalidFWorldException extends InvalidValueException {
        public InvalidFWorldException(@NotNull String input) {
            super(input);
        }
    }
}
