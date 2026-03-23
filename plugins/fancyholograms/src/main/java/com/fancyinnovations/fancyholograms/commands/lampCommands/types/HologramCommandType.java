package com.fancyinnovations.fancyholograms.commands.lampCommands.types;

import com.fancyinnovations.fancyholograms.api.FancyHolograms;
import com.fancyinnovations.fancyholograms.api.HologramRegistry;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.BukkitExceptionHandler;
import revxrsal.commands.exception.InvalidValueException;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

import java.util.Optional;

public class HologramCommandType extends BukkitExceptionHandler implements ParameterType<BukkitCommandActor, Hologram> {

    public static final HologramCommandType INSTANCE = new HologramCommandType();
    private static final HologramRegistry REGISTRY = FancyHolograms.get().getRegistry();

    private HologramCommandType() {
    }

    @Override
    public Hologram parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<@NotNull BukkitCommandActor> context) {
        String id = input.readString();

        Optional<Hologram> hologram = REGISTRY.get(id);
        if (hologram.isPresent()) {
            return hologram.get();
        }

        throw new InvalidHologramException(id);
    }

    @HandleException
    public void onInvalidHologram(InvalidHologramException e, BukkitCommandActor actor) {
        FancyHologramsPlugin.get().getTranslator()
                .translate("common.hologram.not_found")
                .withPrefix()
                .replace("name", e.input())
                .send(actor.sender());
    }

    @Override
    public @NotNull SuggestionProvider<@NotNull BukkitCommandActor> defaultSuggestions() {
        return (ctx) -> REGISTRY.getAll().stream()
                .map(hologram -> hologram.getData().getName())
                .toList();
    }

    public static class InvalidHologramException extends InvalidValueException {
        public InvalidHologramException(@NotNull String input) {
            super(input);
        }
    }

}
