package de.oliver.fancynpcs.commands.lampCommands.suggestions;

import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.List;

public final class DisplayNameSuggestion implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull java.util.Collection<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return List.of("@none");
    }
}
