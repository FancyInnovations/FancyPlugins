package com.fancyinnovations.fancyworlds.commands.world;

import com.fancyinnovations.fancyworlds.utils.FancyContext;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Range;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.command.ExecutableCommand;
import revxrsal.commands.help.Help;

import java.util.List;

public class WorldHelpCMD extends FancyContext {

    public static final WorldHelpCMD INSTANCE = new WorldHelpCMD();
    private static final int ENTRIES_PER_PAGE = 7;

    @Command("world help")
    @Description("Shows a list of all loaded worlds")
    @CommandPermission("fancyworlds.commands.world.help")
    public void help(
            BukkitCommandActor actor,
            @Range(min = 1) @Default("1") int page,
            Help.RelatedCommands<BukkitCommandActor> commands
    ) {
        int maxPages = commands.numberOfPages(ENTRIES_PER_PAGE);

        translator.translate("commands.world.help.header")
                .withPrefix()
                .replace("currentPage", String.valueOf(page))
                .replace("totalPages", String.valueOf(maxPages))
                .send(actor.sender());

        List<ExecutableCommand<BukkitCommandActor>> list = commands.paginate(page, ENTRIES_PER_PAGE);
        for (ExecutableCommand<BukkitCommandActor> command : list) {
            translator.translate("commands.world.help.entry")
                    .replace("description", command.description())
                    .replace("usage", command.usage())
                    .send(actor.sender());
        }
    }

}
