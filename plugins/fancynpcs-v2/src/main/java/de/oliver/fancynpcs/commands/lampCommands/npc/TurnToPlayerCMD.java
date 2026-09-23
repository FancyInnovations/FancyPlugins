package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class TurnToPlayerCMD extends FancyContext {
    public static final TurnToPlayerCMD INSTANCE = new TurnToPlayerCMD();

    private TurnToPlayerCMD() {
    }

    @Command("npc turn_to_player <npc>")
    @CommandPermission("fancynpcs.command.npc.turn_to_player")
    public void onTurnToPlayer(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final @Optional @Nullable Boolean state
    ) {
        final CommandSender sender = actor.sender();
        if (state != null && npc.getData().isTurnToPlayer() != state) {
            if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.TURN_TO_PLAYER, state, sender).callEvent()) {
                npc.getData().setTurnToPlayer(state);
                translator.translate(state ? "npc_turn_to_player_set_true" : "npc_turn_to_player_set_false")
                        .withPrefix()
                        .replace("npc", npc.getData().getName())
                        .send(sender);
            } else {
                translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
            }
        } else if (state == null) {
            // If no state provided, just display current state
            boolean currentState = npc.getData().isTurnToPlayer();
            translator.translate(currentState ? "npc_turn_to_player_set_true" : "npc_turn_to_player_set_false")
                    .withPrefix()
                    .replace("npc", npc.getData().getName())
                    .send(sender);
        }
    }
}
