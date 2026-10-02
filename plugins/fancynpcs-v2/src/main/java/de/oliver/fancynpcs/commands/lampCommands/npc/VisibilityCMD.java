package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.data.property.NpcVisibility;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class VisibilityCMD extends FancyContext {
    public static final VisibilityCMD INSTANCE = new VisibilityCMD();

    private VisibilityCMD() {
    }

    @Command("npc visibility <npc> <visibility>")
    @CommandPermission("fancynpcs.command.npc.visibility")
    public void onVisibility(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final @NotNull NpcVisibility visibility
    ) {
        final CommandSender sender = actor.sender();
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.VISIBILITY, visibility, sender).callEvent()) {
            npc.getData().setVisibility(visibility);

            npc.checkAndUpdateVisibilityForAll();

            translator.translate("npc_visibility_set")
                    .withPrefix()
                    .replace("npc", npc.getData().getName())
                    .replace("visibility", visibility.toString())
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
        }
    }
}
