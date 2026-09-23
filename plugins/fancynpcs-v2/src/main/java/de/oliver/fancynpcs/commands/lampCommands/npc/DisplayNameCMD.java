package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import de.oliver.fancynpcs.commands.lampCommands.suggestions.DisplayNameSuggestion;
import de.oliver.fancynpcs.commands.lampCommands.types.GreedyStringCommandType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentIteratorType;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.chatcolorhandler.paper.PaperColor;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.ParseWith;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;
import java.util.stream.StreamSupport;

public final class DisplayNameCMD extends FancyContext {
    public static final DisplayNameCMD INSTANCE = new DisplayNameCMD();
    // Storing in a static variable to avoid re-creating the array each time suggestion is requested.
    private static final List<String> NONE_SUGGESTIONS = List.of("@none");

    private DisplayNameCMD() {
    }

    @Command("npc displayname <npc> <name>")
    @CommandPermission("fancynpcs.command.npc.displayname")
    public void onDisplayName(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final @NotNull @SuggestWith(DisplayNameSuggestion.class) @ParseWith(GreedyStringCommandType.class) String name
    ) {
        final CommandSender sender = actor.sender();
        // Finalizing the name. In case input is '@none', it gets replaced with '<empty>' for backwards compatibility.
        final String finalName = name.equalsIgnoreCase("@none") ? "<empty>" : name;
        // Sending error message in case banned command has been found in the input.
        if (hasBlockedCommands(finalName)) {
            translator.translate("command_input_contains_blocked_command").withPrefix().send(sender);
            return;
        }
        // Calling the event and updating the state if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.DISPLAY_NAME, finalName, sender).callEvent()) {
            npc.getData().setDisplayName(finalName);
            npc.updateForAll();
            translator.translate(finalName.equalsIgnoreCase("<empty>") ? "npc_displayname_set_empty" : "npc_displayname_set_name")
                    .withPrefix()
                    .replace("npc", npc.getData().getName())
                    .replace("name", finalName)
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
        }
    }

    /* UTILITY METHODS */

    /**
     * Returns {@code true} if specified component contains blocked command, {@code false} otherwise.
     */
    public boolean hasBlockedCommands(final @NotNull String message) {
        // Converting message to a Component.
        final Component component = PaperColor.handler().translate(message);
        // Getting the list of all blocked commands.
        final List<String> blockedCommands = config.getBlockedCommands();
        // Iterating over all elements of the component.
        return StreamSupport.stream(component.iterable(ComponentIteratorType.DEPTH_FIRST).spliterator(), false).anyMatch(it -> {
            final ClickEvent event = it.clickEvent();
            // We only care about click events with run_command as an action. Continuing if not found.
            if (event == null || event.action() != ClickEvent.Action.RUN_COMMAND)
                return false;
            // Iterating over list of blocked commands...
            for (final String blockedCommand : blockedCommands) {
                // Transforming the command to a base command with trailed whitespaces and slashes. This also removes namespaced part from the beginning of the command.
                final String transformedBaseCommand = blockedCommand.replace('/', ' ').strip().split(" ")[0].replaceAll(".*?:+", "");
                // Comparing click event value with the transformed base command. Returning the result.
                if (((ClickEvent.Payload.Text) event.payload()).value().replace('/', ' ').strip().split(" ")[0].replaceAll(".*?:+", "").equalsIgnoreCase(transformedBaseCommand))
                    return true;
            }
            // Returning false as no blocked commands has been found.
            return false;
        });
    }

}
