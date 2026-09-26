package com.fancyinnovations.fancyeconomy.commands;

import com.fancyinnovations.fancyeconomy.currencies.CurrencyPlayerManager;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.Collection;
import java.util.Arrays;

public class AllPlayersSuggestion implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull Collection<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        return Arrays.asList(CurrencyPlayerManager.getAllPlayerNames());
    }
}
