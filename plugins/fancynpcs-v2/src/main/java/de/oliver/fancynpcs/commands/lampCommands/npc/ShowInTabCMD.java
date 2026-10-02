package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class ShowInTabCMD extends FancyContext {
    public static final ShowInTabCMD INSTANCE = new ShowInTabCMD();

    private ShowInTabCMD() {
    }

    @Command("npc show_in_tab <npc>")
    @CommandPermission("fancynpcs.command.npc.show_in_tab")
    public void onCommand(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final @Optional @Nullable Boolean state
    ) {
        final CommandSender sender = actor.sender();
        final boolean finalState = (state == null) ? !npc.getData().isShowInTab() : state;
        // Calling the event and updating the state if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SHOW_IN_TAB, finalState, sender).callEvent()) {
            npc.getData().setShowInTab(finalState);
            npc.removeForAll();
            npc.create();
            npc.spawnForAll();
            translator.translate(finalState ? "npc_show_in_tab_set_true" : "npc_show_in_tab_set_false").withPrefix().replace("npc", npc.getData().getName()).send(sender);
            return;
        }
        // Otherwise, sending error message to the sender.
        translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
    }

}
