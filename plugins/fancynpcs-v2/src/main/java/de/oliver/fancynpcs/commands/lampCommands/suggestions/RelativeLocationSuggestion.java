package de.oliver.fancynpcs.commands.lampCommands.suggestions;

import org.bukkit.FluidCollisionMode;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;

public final class RelativeLocationSuggestion implements SuggestionProvider<BukkitCommandActor> {
    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");

    @Override
    public @NotNull Collection<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        if (!context.actor().isPlayer()) return List.of();

        RayTraceResult result = context.actor().requirePlayer().rayTraceBlocks(32.0, FluidCollisionMode.ALWAYS);
        if (result == null) return List.of("~ ~ ~");

        return List.of(
                FORMAT.format(result.getHitPosition().getX()) + " " +
                        FORMAT.format(result.getHitPosition().getY()) + " " +
                        FORMAT.format(result.getHitPosition().getZ()),
                "~ ~ ~"
        );
    }
}
