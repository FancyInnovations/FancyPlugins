package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.events.NpcCreateEvent;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// TO-DO: Console support with --position and --world parameter flags.
public final class CopyCMD extends FancyContext {
    public static final CopyCMD INSTANCE = new CopyCMD();
    private static final Pattern NPC_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9/_-]*$");

    private CopyCMD() {
    }

    @Command("npc copy <npc> <name>")
    @CommandPermission("fancynpcs.command.npc.copy")
    public void onCopy(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final @NotNull String name
    ) {
        final Player sender = actor.requirePlayer();
        // Sending error message if name does not match configured pattern.
        if (!NPC_NAME_PATTERN.matcher(name).find()) {
            translator.translate("npc_create_failure_invalid_name").withPrefix().replaceStripped("name", name).send(sender);
            return;
        }
        // Creating a copy of an NPC and all it's data. The only different thing is it's UUID.
        final Npc copied = plugin.getNpcAdapter().apply(
                new NpcData(
                        UUID.randomUUID().toString(),
                        name,
                        sender.getUniqueId(),
                        npc.getData().getDisplayName(),
                        npc.getData().getSkinData(),
                        sender.getLocation().clone(),
                        npc.getData().isShowInTab(),
                        npc.getData().isSpawnEntity(),
                        npc.getData().isCollidable(),
                        npc.getData().isGlowing(),
                        npc.getData().getGlowingColor(),
                        npc.getData().getType(),
                        new ConcurrentHashMap<>(npc.getData().getEquipment()),
                        npc.getData().isTurnToPlayer(),
                        npc.getData().getTurnToPlayerDistance(),
                        npc.getData().getOnClick(),
                        npc.getData().getActions()
                                .entrySet()
                                .stream()
                                .collect(Collectors.toConcurrentMap(
                                        Map.Entry::getKey,
                                        e -> new ArrayList<>(e.getValue())
                                )),
                        npc.getData().getInteractionCooldown(),
                        npc.getData().getScale(),
                        npc.getData().getVisibilityDistance(),
                        new ConcurrentHashMap<>(npc.getData().getAttributes()),
                        npc.getData().isMirrorSkin()
                ));
        // Calling the event and creating + registering copied NPC if not cancelled.
        if (new NpcCreateEvent(copied, sender).callEvent()) {
            copied.create();
            plugin.getNpcManagerImpl().registerNpc(copied);
            copied.spawnForAll();
            translator.translate("npc_copy_success").withPrefix().replace("npc", npc.getData().getName()).replace("new_npc", copied.getData().getName()).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
        }
    }
}
