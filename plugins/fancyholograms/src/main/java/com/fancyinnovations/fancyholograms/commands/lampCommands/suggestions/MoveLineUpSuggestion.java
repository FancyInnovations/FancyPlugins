package com.fancyinnovations.fancyholograms.commands.lampCommands.suggestions;

import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

public class MoveLineUpSuggestion implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull Collection<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        Hologram hologram = context.getResolvedArgumentOrNull(Hologram.class);
        if (hologram == null || !(hologram.getData() instanceof TextHologramData textData)) {
            return List.of();
        }

        return IntStream.range(2, textData.getText().size() + 1)
                .mapToObj(Integer::toString)
                .toList();
    }
}