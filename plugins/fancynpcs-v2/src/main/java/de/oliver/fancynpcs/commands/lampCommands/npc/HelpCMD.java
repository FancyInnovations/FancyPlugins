package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancylib.translations.message.MultiMessage;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import de.oliver.fancynpcs.commands.lampCommands.suggestions.HelpPageSuggestion;
import org.bukkit.command.CommandSender;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class HelpCMD extends FancyContext {
    public static final HelpCMD INSTANCE = new HelpCMD();

    private HelpCMD() {
    }

    @Command("npc help")
    @CommandPermission("fancynpcs.command.npc")
    public void onHelp(
            final BukkitCommandActor actor,
            final @SuggestWith(HelpPageSuggestion.class) @Default("1") int page
    ) {
        final CommandSender sender = actor.sender();
        // Getting the (full) help contents.
        final MultiMessage contents = (MultiMessage) translator.translate("npc_help_contents");
        // Calculating max page number.
        final int maxPage = (int) Math.ceil(contents.getRawMessages().size() / 6F);
        // Getting the requested page. Defaults to 1 for invalid input and is capped by number of the last page.
        final int finalPage = Math.clamp(page, 1, maxPage);
        // Sending help contents to the sender.
        translator.translate("npc_help_page_header").replace("page", String.valueOf(finalPage)).replace("max_page", String.valueOf(maxPage)).send(sender);
        contents.page(finalPage, 6).send(sender);
        translator.translate("npc_help_page_footer").replace("page", String.valueOf(finalPage)).replace("max_page", String.valueOf(maxPage)).send(sender);
    }


}
