package de.oliver.fancynpcs.commands.lampCommands.suggestions;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.ArrayList;
import java.util.Collection;

public final class ActionNumberSuggestion implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull Collection<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        Npc npc = context.getResolvedArgumentOrNull("npc");
        ActionTrigger trigger = context.getResolvedArgumentOrNull("trigger");
        if (npc == null || trigger == null) return java.util.List.of();

        ArrayList<String> result = new ArrayList<>();
        for (int i = 1; i <= npc.getData().getActions(trigger).size(); i++) {
            result.add(String.valueOf(i));
        }

        return result;
    }
}
