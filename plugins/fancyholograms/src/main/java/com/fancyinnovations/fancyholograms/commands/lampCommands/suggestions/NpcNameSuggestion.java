package com.fancyinnovations.fancyholograms.commands.lampCommands.suggestions;

import com.fancyinnovations.fancyholograms.util.PluginUtils;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.Collection;
import java.util.List;

public class NpcNameSuggestion implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull Collection<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        if (!PluginUtils.isFancyNpcsEnabled()) {
            return List.of();
        }

        return FancyNpcsPlugin.get().getNpcManager().getAllNpcs().stream()
                .map(npc -> npc.getData().getName())
                .toList();
    }
}
