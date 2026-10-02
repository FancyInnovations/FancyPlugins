package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class ScaleCMD extends FancyContext {
    public static final ScaleCMD INSTANCE = new ScaleCMD();

    private ScaleCMD() {
    }

    @Command("npc scale <npc> <factor>")
    @CommandPermission("fancynpcs.command.npc.scale")
    public void onScale(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final float factor
    ) {
        final CommandSender sender = actor.sender();
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SCALE, factor, sender).callEvent()) {
            npc.getData().setScale(factor);
            npc.updateForAll();
            translator.translate("npc_scale_set_success")
                    .withPrefix()
                    .replace("npc", npc.getData().getName())
                    .replace("scale", String.valueOf(factor))
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
        }
    }
}
