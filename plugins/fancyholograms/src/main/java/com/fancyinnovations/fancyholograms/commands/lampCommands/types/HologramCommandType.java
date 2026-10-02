package com.fancyinnovations.fancyholograms.commands.lampCommands.types;

import com.fancyinnovations.fancyholograms.api.FancyHolograms;
import com.fancyinnovations.fancyholograms.api.HologramRegistry;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.lampCommands.hologram.SelectCMD;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.BukkitExceptionHandler;
import revxrsal.commands.exception.InvalidValueException;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class HologramCommandType extends BukkitExceptionHandler implements ParameterType<BukkitCommandActor, Hologram> {

    public static final HologramCommandType INSTANCE = new HologramCommandType();
    private static final HologramRegistry REGISTRY = FancyHolograms.get().getRegistry();

    private HologramCommandType() {
    }

    @Override
    public Hologram parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<@NotNull BukkitCommandActor> context) {
        String id = input.readString();

        if (id.equalsIgnoreCase(".selected")) {
            Optional<Hologram> selectedHologram = SelectCMD.getSelectedHologram(context.actor().requirePlayer());
            if (selectedHologram.isPresent()) {
                return selectedHologram.get();
            } else {
                throw new InvalidHologramException(id);
            }
        } else if (id.equalsIgnoreCase(".nearest")) {
            Player player = context.actor().requirePlayer();

            Collection<Hologram> holograms = REGISTRY.getAll();
            if (holograms.isEmpty()) {
                throw new InvalidHologramException(id);
            }

            Hologram nearestHologram = null;
            double nearestDistance = Double.MAX_VALUE;
            for (Hologram hologram : holograms) {
                if (hologram.getData().getWorldName() == null || !hologram.getData().getWorldName().equals(player.getLocation().getWorld().getName())) {
                    continue; // Skip holograms in different worlds
                }

                double distance = hologram.getData().getLocation().distanceSquared(player.getLocation());
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestHologram = hologram;
                }
            }

            if (nearestHologram == null) {
                throw new InvalidHologramException(id);
            }
            return nearestHologram;
        }

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
        return (ctx) -> {
            List<String> names = new ArrayList<>();
            names.add(".selected");
            names.add(".nearest");

            for (Hologram hologram : REGISTRY.getAll()) {
                names.add(hologram.getData().getName());
            }

            return names;
        };
    }

    public static class InvalidHologramException extends InvalidValueException {
        public InvalidHologramException(@NotNull String input) {
            super(input);
        }
    }

}
