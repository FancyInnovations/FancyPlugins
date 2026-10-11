package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.UUID;
import java.util.regex.Pattern;

public final class RenameCMD extends FancyContext {
    public static final RenameCMD INSTANCE = new RenameCMD();
    private static final Pattern NPC_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9/_-]*$");
    private static final UUID EMPTY_UUID = new UUID(0, 0);

    private RenameCMD() {
    }

    @Command("npc rename <npc> <name>")
    @CommandPermission("fancynpcs.command.npc.rename")
    public void onRename(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final @NotNull String name
    ) {
        final CommandSender sender = actor.sender();
        if (!NPC_NAME_PATTERN.matcher(name).find()) {
            translator.translate("npc_create_failure_invalid_name").withPrefix().replaceStripped("name", name).send(sender);
            return;
        }

        final UUID creator = (sender instanceof Player player) ? player.getUniqueId() : EMPTY_UUID;
        final boolean alreadyExists = FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled()
                ? plugin.getNpcManager().getNpc(name, creator) != null
                : plugin.getNpcManager().getNpc(name) != null;
        if (alreadyExists) {
            translator.translate("npc_create_failure_already_exists").withPrefix().replace("npc", name).send(sender);
            return;
        }

        final String oldName = npc.getData().getName();
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.NAME, name, sender).callEvent()) {
            plugin.getNpcManager().renameNpc(npc, name);
            translator.translate("npc_rename_success").withPrefix().replace("npc", oldName).replace("new_npc", name).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
        }
    }
}
