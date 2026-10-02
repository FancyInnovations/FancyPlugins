package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class TeleportCMD extends FancyContext {
    public static final TeleportCMD INSTANCE = new TeleportCMD();

    private TeleportCMD() {
    }

    @Command("npc teleport <npc>")
    @CommandPermission("fancynpcs.command.npc.teleport")
    public void onTeleport(
            final BukkitCommandActor actor,
            final @NotNull Npc npc
    ) {
        final Player sender = actor.requirePlayer();
        final Location location = npc.getData().getLocation();
        // Checking if the world is still loaded.
        if (location.getWorld() == null) {
            translator.translate("npc_teleport_failure_world_not_loaded").withPrefix().send(sender);
            return;
        }
        // Teleporting and sending message to the sender. This operation can occasionally fail.
        sender.teleportAsync(location).whenComplete((isSuccess, thr) -> {
            translator.translate(isSuccess ? "npc_teleport_success" : "npc_teleport_failure_exception").replace("npc", npc.getData().getName()).withPrefix().send(sender);
            // Printing stacktrace to the console in case an exception occurred.
            if (thr != null)
                thr.printStackTrace();
        });
    }

}
