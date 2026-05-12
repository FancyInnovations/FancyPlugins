package com.fancyinnovations.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import com.fancyinnovations.fancynpcs.FancyNpcs;
import com.fancyinnovations.fancynpcs.api.Npc;
import com.fancyinnovations.fancynpcs.api.events.NpcModifyEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentIteratorType;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.chatcolorhandler.ModernChatColorHandler;

import java.util.List;
import java.util.stream.StreamSupport;

public enum DisplayNameCMD {
    INSTANCE; // SINGLETON

    // Storing in a static variable to avoid re-creating the array each time suggestion is requested.
    private static final List<String> NONE_SUGGESTIONS = List.of("@none");
    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc displayname <npc> scale <scale>")
    @Permission("fancynpcs.command.npc.displayname")
    public void onDisplayNameScale(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final float scale
    ) {
        // Validate scale value
        if (scale <= 0) {
            translator.translate("npc_displayname_scale_invalid").send(sender);
            return;
        }

        // Calling the event and updating the state if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.DISPLAY_NAME, scale, sender).callEvent()) {
            npc.getData().setDisplayNameScale(scale);
            npc.updateForAll();
            translator.translate("npc_displayname_scale_set")
                    .replace("npc", npc.getData().getName())
                    .replace("scale", String.valueOf(scale))
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    @Command("npc displayname <npc> text <name>")
    @Permission("fancynpcs.command.npc.displayname")
    public void onDisplayNameText(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull @Argument(suggestions = "DisplayNameCMD/none") @Greedy String name
    ) {
        onDisplayName(sender, npc, name);
    }

    @Command("npc displayname <npc> <name>")
    @Permission("fancynpcs.command.npc.displayname")
    public void onDisplayName(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull @Argument(suggestions = "DisplayNameCMD/none") @Greedy String name
    ) {
        // Finalizing the name. In case input is '@none', it gets replaced with '<empty>' for backwards compatibility.
        final String finalName = name.equalsIgnoreCase("@none") ? "<empty>" : name;
        // Sending error message in case banned command has been found in the input.
        if (hasBlockedCommands(finalName)) {
            translator.translate("command_input_contains_blocked_command").send(sender);
            return;
        }
        // Calling the event and updating the state if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.DISPLAY_NAME, finalName, sender).callEvent()) {
            npc.getData().setDisplayName(finalName);
            npc.updateForAll();
            translator.translate(finalName.equalsIgnoreCase("<empty>") ? "npc_displayname_set_empty" : "npc_displayname_set_name")
                    .replace("npc", npc.getData().getName())
                    .replace("name", finalName)
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    /* PARSERS AND SUGGESTIONS */

    @Suggestions("DisplayNameCMD/none")
    public List<String> suggestNone(final CommandContext<CommandSender> sender, CommandInput input) {
        return NONE_SUGGESTIONS;
    }

    /* UTILITY METHODS */

    /**
     * Returns {@code true} if specified component contains blocked command, {@code false} otherwise.
     */
    public boolean hasBlockedCommands(final @NotNull String message) {
        // Converting message to a Component.
        final Component component = ModernChatColorHandler.translate(message);
        // Getting the list of all blocked commands.
        final List<String> blockedCommands = FancyNpcs.getInstance().getFancyNpcConfig().getBlockedCommands();
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
                if (event.value().replace('/', ' ').strip().split(" ")[0].replaceAll(".*?:+", "").equalsIgnoreCase(transformedBaseCommand))
                    return true;
            }
            // Returning false as no blocked commands has been found.
            return false;
        });
    }

}
