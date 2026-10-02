package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class CenterCMD extends FancyContext {
    public static final CenterCMD INSTANCE = new CenterCMD();

    private CenterCMD() {
    }

    @Command("npc center <npc>")
    @CommandPermission("fancynpcs.command.npc.center")
    public void onCenter(
            final BukkitCommandActor actor,
            final @NotNull Npc npc
    ) {
        final CommandSender sender = actor.sender();
        NpcData npcData = npc.getData();
        Location location = npcData.getLocation();

        if (location == null) {
            translator.translate("npc_center_failure_no_location").withPrefix().replace("npc", npcData.getName()).send(sender);
            return;
        }

        // Center the NPC on the block
        Location centeredLocation = location.clone();
        centeredLocation.setX(centeredLocation.getBlockX() + 0.5);
        centeredLocation.setY(centeredLocation.getY());
        centeredLocation.setZ(centeredLocation.getBlockZ() + 0.5);

        // Trigger the modify event
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.LOCATION, centeredLocation, sender).callEvent()) {
            npcData.setLocation(centeredLocation);
            npc.updateForAll();

            translator.translate("npc_center_success")
                    .withPrefix()
                    .replace("npc", npcData.getName())
                    .replace("x", String.format("%.2f", centeredLocation.getX()))
                    .replace("y", String.format("%.2f", centeredLocation.getY()))
                    .replace("z", String.format("%.2f", centeredLocation.getZ()))
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
        }
    }
}
