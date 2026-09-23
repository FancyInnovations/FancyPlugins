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

public final class CollidableCMD extends FancyContext {
    public static final CollidableCMD INSTANCE = new CollidableCMD();

    private CollidableCMD() {
    }

    @Command("npc collidable <npc>")
    @CommandPermission("fancynpcs.command.npc.collidable")
    public void onCollidable(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final @Optional @Nullable Boolean state
    ) {
        final CommandSender sender = actor.sender();
        // Finalizing the state. If no state has been specified, the current one is inverted.
        final boolean finalState = (state == null) ? !npc.getData().isCollidable() : state;
        // Calling the event and updating the state if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.COLLIDABLE, finalState, sender).callEvent()) {
            npc.getData().setCollidable(finalState);
            npc.removeForAll();
            npc.spawnForAll();
            translator.translate(finalState ? "npc_collidable_set_true" : "npc_collidable_set_false").withPrefix().replace("npc", npc.getData().getName()).send(sender);
            return;
        }
        translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
    }

}
