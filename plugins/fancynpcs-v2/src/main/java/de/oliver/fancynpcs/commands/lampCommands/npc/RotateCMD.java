package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class RotateCMD extends FancyContext {
    public static final RotateCMD INSTANCE = new RotateCMD();

    private RotateCMD() {
    }

    @Command("npc rotate <npc> <yaw> <pitch>")
    @CommandPermission("fancynpcs.command.npc.rotate")
    public void onRotate(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final float yaw,
            final float pitch
    ) {
        final CommandSender sender = actor.sender();
        final Location currentLocation = npc.getData().getLocation();
        final Location newLocation = currentLocation.clone();
        newLocation.setYaw(yaw);
        newLocation.setPitch(pitch);

        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.ROTATION, new float[]{yaw, pitch}, sender).callEvent()) {
            npc.getData().setLocation(newLocation);
            npc.updateForAll();
            translator.translate("npc_rotate_set_success")
                    .withPrefix()
                    .replace("npc", npc.getData().getName())
                    .replace("yaw", String.valueOf(yaw))
                    .replace("pitch", String.valueOf(pitch))
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
        }
    }
}
