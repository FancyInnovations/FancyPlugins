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

public final class TurnToPlayerDistanceCMD extends FancyContext {
    public static final TurnToPlayerDistanceCMD INSTANCE = new TurnToPlayerDistanceCMD();
    // Storing in a static variable to avoid re-creating the array each time suggestion is requested.
    private final List<String> DISTANCE_SUGGESTIONS = List.of("default");


    private TurnToPlayerDistanceCMD() {
    }

    @Command("npc turn_to_player_distance <npc> <distance>")
    @CommandPermission("fancynpcs.command.npc.turn_to_player_distance")
    public void onTurnToPlayerDistance(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final int distance
    ) {
        final CommandSender sender = actor.sender();
        if (distance < -1) {
            translator.translate("npc_turn_to_player_distance_invalid").withPrefix().send(sender);
            return;
        }

        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.TURN_TO_PLAYER_DISTANCE, distance, sender).callEvent()) {
            npc.getData().setTurnToPlayerDistance(distance);

            if (distance == -1) {
                // Using default distance
                int defaultDistance = config.getTurnToPlayerDistance();
                translator.translate("npc_turn_to_player_distance_default")
                        .withPrefix()
                        .replace("npc", npc.getData().getName())
                        .replace("distance", String.valueOf(defaultDistance))
                        .send(sender);
            } else {
                // Using custom distance
                translator.translate("npc_turn_to_player_distance_set")
                        .withPrefix()
                        .replace("npc", npc.getData().getName())
                        .replace("distance", String.valueOf(distance))
                        .send(sender);
            }
        } else {
            translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
        }
    }


}
