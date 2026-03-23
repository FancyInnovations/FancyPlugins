package com.fancyinnovations.fancyholograms.commands.lampCommands.types;

import com.fancyinnovations.fancyholograms.api.FancyHolograms;
import com.fancyinnovations.fancyholograms.api.trait.HologramTraitRegistry;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.BukkitExceptionHandler;
import revxrsal.commands.exception.InvalidValueException;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public class TraitCommandType extends BukkitExceptionHandler implements ParameterType<BukkitCommandActor, HologramTraitRegistry.TraitInfo> {

    public static final TraitCommandType INSTANCE = new TraitCommandType();
    private static final HologramTraitRegistry REGISTRY = FancyHolograms.get().getTraitRegistry();

    private TraitCommandType() {
    }

    @Override
    public HologramTraitRegistry.TraitInfo parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<@NotNull BukkitCommandActor> context) {
        String id = input.readString();

        HologramTraitRegistry.TraitInfo trait = REGISTRY.getTrait(id);
        if (trait != null) {
            return trait;
        }

        throw new InvalidTraitException(id);
    }

    @HandleException
    public void onInvalidTrait(InvalidTraitException e, BukkitCommandActor actor) {
        FancyHologramsPlugin.get().getTranslator()
                .translate("commands.hologram.edit.trait.not_found")
                .withPrefix()
                .replace("name", e.input())
                .send(actor.sender());
    }

    @Override
    public @NotNull SuggestionProvider<@NotNull BukkitCommandActor> defaultSuggestions() {
        return (ctx) -> REGISTRY.getTraits().stream()
                .map(HologramTraitRegistry.TraitInfo::name)
                .toList();
    }

    public static class InvalidTraitException extends InvalidValueException {
        public InvalidTraitException(@NotNull String input) {
            super(input);
        }
    }

}
