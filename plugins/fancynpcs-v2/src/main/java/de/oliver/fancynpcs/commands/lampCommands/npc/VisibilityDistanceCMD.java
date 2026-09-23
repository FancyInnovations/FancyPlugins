package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;

public final class VisibilityDistanceCMD extends FancyContext {
    public static final VisibilityDistanceCMD INSTANCE = new VisibilityDistanceCMD();
    // Storing in a static variable to avoid re-creating the array each time suggestion is requested.
    private final List<String> DISTANCE_SUGGESTIONS = List.of("always_visible", "default", "not_visible");


    private VisibilityDistanceCMD() {
    }

    @Command("npc visibility_distance <npc> <distance>")
    @CommandPermission("fancynpcs.command.npc.visibility_distance")
    public void onVisibilityDistance(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final int distance
    ) {
        final CommandSender sender = actor.sender();
        final int finalDistance = Math.clamp(distance, -1, Integer.MAX_VALUE);
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.VISIBILITY_DISTANCE, distance, sender).callEvent()) {
            npc.getData().setVisibilityDistance(finalDistance);
            npc.updateForAll();
            translator.translate(finalDistance == -1 ? "npc_visibility_distance_set_default" : finalDistance == 0 ? "npc_visibility_distance_set_not_visible" : finalDistance == Integer.MAX_VALUE ? "npc_visibility_distance_set_always_visible" : "npc_visibility_distance_set_value")
                    .withPrefix()
                    .replace("npc", npc.getData().getName())
                    .replace("distance", (finalDistance > -1) ? String.valueOf(finalDistance) : String.valueOf(config.getVisibilityDistance()))
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
        }
    }


}
