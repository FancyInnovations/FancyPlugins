package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import de.oliver.fancynpcs.commands.lampCommands.suggestions.RelativeLocationSuggestion;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Flag;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.text.DecimalFormat;

public final class MoveToCMD extends FancyContext {
    public static final MoveToCMD INSTANCE = new MoveToCMD();
    private static final DecimalFormat COORDS_FORMAT = new DecimalFormat("#.##");

    private MoveToCMD() {
    }

    @Command("npc move_to <npc> <location>")
    @CommandPermission("fancynpcs.command.npc.move_to")
    public void onCommand(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final @NotNull @SuggestWith(RelativeLocationSuggestion.class) Location location,
            final @Optional @Nullable World world,
            final @Optional @Flag("look-in-my-direction") boolean shouldLookInSenderDirection
    ) {
        final CommandSender sender = actor.sender();
        // Finalizing World argument. Player-like senders don't have to specify the 'world' argument which then defaults to the World sender is currently in.
        final World finalWorld = (world == null && sender instanceof Player player) ? player.getWorld() : world;
        // Sending error message if finalized World argument ended up being null. This can happen when command is executed by console and 'world' argument was not specified.
        if (finalWorld == null) {
            translator.translate("npc_move_to_failure_must_specify_world").withPrefix().send(sender);
            return;
        }
        // Updating World of the finalized Location. This should never pass a null value.
        location.setWorld(finalWorld);
        // Updating direction NPC will be looking at. Only if '--look-in-my-direction' is present and sender is player.
        if (shouldLookInSenderDirection && sender instanceof Player player)
            location.setDirection(player.getLocation().subtract(location).toVector());
        // Calling the event and re-locating NPC if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.LOCATION, location, sender).callEvent()) {
            npc.getData().setLocation(location);
            npc.updateForAll();
            translator.translate("npc_move_to_success")
                    .withPrefix()
                    .replace("npc", npc.getData().getName())
                    .replace("x", COORDS_FORMAT.format(location.x()))
                    .replace("y", COORDS_FORMAT.format(location.y()))
                    .replace("z", COORDS_FORMAT.format(location.z()))
                    .replace("world", finalWorld.getName())
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
        }
    }
}
