package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcRemoveEvent;
import de.oliver.fancynpcs.api.events.NpcStopLookingEvent;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class RemoveCMD extends FancyContext {
    public static final RemoveCMD INSTANCE = new RemoveCMD();

    private RemoveCMD() {
    }

    @Command("npc remove <npc>")
    @CommandPermission("fancynpcs.command.npc.remove")
    public void onRemove(
            final BukkitCommandActor actor,
            final @NotNull Npc npc
    ) {
        final CommandSender sender = actor.sender();
        // Calling the event and removing the NPC if not cancelled.
        if (new NpcRemoveEvent(npc, sender).callEvent()) {
            npc.removeForAll();
            // Iterating over all online players that the NPC is currently looking at.
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (npc.getIsLookingAtPlayer().getOrDefault(onlinePlayer.getUniqueId(), false)) {
                    // Changing state as Npc#getIsLookingAtPlayer#get(...) called within the event listener should return false now.
                    npc.getIsLookingAtPlayer().put(onlinePlayer.getUniqueId(), false);
                    // Calling the NpcStopLookingEvent event.
                    new NpcStopLookingEvent(npc, onlinePlayer).callEvent();
                }
            }
            plugin.getNpcManagerImpl().removeNpc(npc);
            translator.translate("npc_remove_success").withPrefix().replace("npc", npc.getData().getName()).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
        }
    }

}
