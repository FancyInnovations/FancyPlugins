package com.fancyinnovations.fancydialogs.commands;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import de.oliver.fancylib.translations.Translator;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class TutorialCMD {

    public static final TutorialCMD INSTANCE = new TutorialCMD();

    private final FancyDialogsPlugin plugin = FancyDialogsPlugin.get();
    private final Translator translator = FancyDialogsPlugin.get().getTranslator();

    private TutorialCMD() {
    }

    @Command("tutorial open <tutorial>")
    @Description("Opens a specific tutorial dialog")
    @CommandPermission("fancydialogs.commands.tutorial")
    public void onTutorialOpen(
            final BukkitCommandActor actor,
            final String tutorial
    ) {
        actor.requirePlayer().sendMessage("Opening tutorial: " + tutorial);
    }
}
