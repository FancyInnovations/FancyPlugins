package com.fancyinnovations.fancydialogs.commands;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import de.oliver.fancylib.translations.Translator;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class TutorialCMD {

    public static final TutorialCMD INSTANCE = new TutorialCMD();

    private final FancyDialogsPlugin plugin = FancyDialogsPlugin.get();
    private final Translator translator = FancyDialogsPlugin.get().getTranslator();

    private TutorialCMD() {
    }

    @Command("tutorial open <tutorial>")
    @CommandPermission("fancydialogs.commands.tutorial")
    public void onOpen(
            final BukkitCommandActor sender,
            final String tutorial
    ) {
        sender.requirePlayer().sendMessage("Opening tutorial: " + tutorial);
    }
}
