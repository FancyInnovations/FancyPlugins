package com.fancyinnovations.fancyholograms.commands.suggestions;

import com.fancyinnovations.fancyholograms.api.FancyHolograms;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.trait.HologramTraitRegistry;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.Collection;
import java.util.List;

public class AttachedTraitsSuggestion implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull Collection<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        Hologram hologram = context.getResolvedArgumentOrNull(Hologram.class);
        if (hologram == null) {
            return List.of();
        }

        return FancyHolograms.get().getTraitRegistry().getTraits().stream()
                .filter(trait -> !trait.isDefault())
                .filter(trait -> !hologram.getData().getTraitTrait().isTraitAttached(trait.clazz()))
                .map(HologramTraitRegistry.TraitInfo::name)
                .toList();
    }
}
