package com.fancyinnovations.fancyholograms.commands.lampCommands.suggestions;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.Arrays;
import java.util.Collection;

public class BlockMaterialSuggestion implements SuggestionProvider<BukkitCommandActor> {
    @Override
    public @NotNull Collection<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .map(Material::name)
                .toList();
    }
}
