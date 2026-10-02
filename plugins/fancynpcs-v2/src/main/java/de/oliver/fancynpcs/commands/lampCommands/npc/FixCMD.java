package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class FixCMD extends FancyContext {
    public static final FixCMD INSTANCE = new FixCMD();

    private FixCMD() {
    }

    @Command("npc fix <npc>")
    @CommandPermission("fancynpcs.command.npc.fix")
    public void onFix(
            final BukkitCommandActor actor,
            final @NotNull Npc npc
    ) {
        final CommandSender sender = actor.sender();
        npc.removeForAll();
        npc.create();
        Bukkit.getOnlinePlayers().forEach(npc::checkAndUpdateVisibility);
        translator.translate("npc_fix_success").withPrefix().replace("npc", npc.getData().getName()).send(sender);
    }

}
