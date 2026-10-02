package com.fancyinnovations.fancyholograms.commands.lampCommands.suggestions;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.Collection;

public class NpcActionSuggestion implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull Collection<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return FancyNpcsPlugin.get().getActionManager().getAllActions().stream()
                .map(action -> action.getName().toLowerCase())
                .toList();
    }
}
