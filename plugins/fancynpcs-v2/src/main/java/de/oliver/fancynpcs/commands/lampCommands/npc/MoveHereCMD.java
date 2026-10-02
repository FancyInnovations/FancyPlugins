package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class MoveHereCMD extends FancyContext {
    public static final MoveHereCMD INSTANCE = new MoveHereCMD();

    private MoveHereCMD() {
    }

    @Command("npc move_here <npc>")
    @CommandPermission("fancynpcs.command.npc.move_here")
    public void onCommand(
            final BukkitCommandActor actor,
            final @NotNull Npc npc
    ) {
        final Player sender = actor.requirePlayer();
        final Location location = sender.getLocation();
        final String oldWorld = npc.getData().getLocation().getWorld().getName();
        // Calling the event and moving the NPc to location of the sender, if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.LOCATION, location, sender).callEvent()) {
            npc.getData().setLocation(location);
            if (oldWorld.equals(location.getWorld().getName())) {
                npc.updateForAll();
            } else {
                npc.removeForAll();
                npc.spawnForAll();
            }
            translator.translate("npc_move_here_success").withPrefix().replace("npc", npc.getData().getName()).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
        }
    }
}
