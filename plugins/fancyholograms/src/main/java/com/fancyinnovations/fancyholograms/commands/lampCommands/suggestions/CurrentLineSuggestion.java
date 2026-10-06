package com.fancyinnovations.fancyholograms.commands.lampCommands.suggestions;

import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.Collection;
import java.util.List;

/**
 * Suggests the current text of the line that is being edited, so that it can be
 * completed and then modified instead of having to be typed from scratch.
 */
public class CurrentLineSuggestion implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull Collection<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        Hologram hologram = context.getResolvedArgumentOrNull(Hologram.class);
        if (hologram == null || !(hologram.getData() instanceof TextHologramData textData)) {
            return List.of();
        }

        Integer line = context.getResolvedArgumentOrNull("line");
        if (line == null || line < 1 || line > textData.getText().size()) {
            return List.of();
        }

        return List.of(textData.getText().get(line - 1));
    }
}
