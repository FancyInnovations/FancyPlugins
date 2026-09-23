package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.events.NpcCreateEvent;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import de.oliver.fancynpcs.commands.lampCommands.suggestions.RelativeLocationSuggestion;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Flag;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.UUID;
import java.util.regex.Pattern;

public final class CreateCMD extends FancyContext {
    public static final CreateCMD INSTANCE = new CreateCMD();
    private static final Pattern NPC_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9/_-]*$");
    private static final UUID EMPTY_UUID = new UUID(0, 0);

    private CreateCMD() {
    }

    @Command("npc create <name>")
    @CommandPermission("fancynpcs.command.npc.create")
    public void onCreate(
            final BukkitCommandActor actor,
            final @NotNull String name,
            final @Optional @Nullable @Flag("type") EntityType type,
            final @Optional @Nullable @Flag(value = "location") @SuggestWith(RelativeLocationSuggestion.class) Location location,
            final @Optional @Nullable @Flag("world") World world
    ) {
        final CommandSender sender = actor.sender();
        // Sending error message if name does not match configured pattern.
        if (!NPC_NAME_PATTERN.matcher(name).find()) {
            translator.translate("npc_create_failure_invalid_name").withPrefix().replaceStripped("name", name).send(sender);
            return;
        }
        // Getting the NPC creator unique identifier. The UUID is always empty (all zeroes) for non-player senders.
        final UUID creator = (sender instanceof Player player) ? player.getUniqueId() : EMPTY_UUID;
        // Sending error message if NPC with such name already exist.
        if (FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() && plugin.getNpcManager().getNpc(name, creator) != null || !FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() && plugin.getNpcManager().getNpc(name) != null) {
            translator.translate("npc_create_failure_already_exists").withPrefix().replace("npc", plugin.getNpcManager().getNpc(name).getData().getName()).send(sender);
            return;
        }
        // Sending error message if sender is console and location has not been specified.
        if (sender instanceof ConsoleCommandSender && location == null) {
            translator.translate("npc_create_failure_must_specify_location").withPrefix().send(sender);
            return;
        }
        // Sending error message if sender is console and world has not been specified.
        if (sender instanceof ConsoleCommandSender && world == null) {
            translator.translate("npc_create_failure_must_specify_world").withPrefix().send(sender);
            return;
        }
        // Finalizing Location argument. This argument is optional and defaults to player's current location.
        final Location finalLocation = (location == null && sender instanceof Player player) ? player.getLocation() : location;
        // Updating World of the Location argument if '--world' flag has been specified.
        if (world != null)
            finalLocation.setWorld(world);
        // Creating new NPC and applying data.
        final Npc npc = plugin.getNpcAdapter().apply(new NpcData(name, creator, finalLocation));
        // Setting the type of NPC. Flag '--type' is optional and defaults to EntityType.PLAYER.
        npc.getData().setType(type != null ? type : EntityType.PLAYER);
        // Calling the event and creating NPC if not cancelled.
        if (new NpcCreateEvent(npc, sender).callEvent()) {
            npc.create();
            plugin.getNpcManagerImpl().registerNpc(npc);
            npc.spawnForAll();
            translator.translate("npc_create_success").withPrefix().replace("npc", name).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
        }
    }

}
