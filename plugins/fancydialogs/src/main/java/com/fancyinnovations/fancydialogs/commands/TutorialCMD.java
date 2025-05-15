package com.fancyinnovations.fancydialogs.commands;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import de.oliver.fancylib.translations.Translator;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public final class TutorialCMD {

    public static final TutorialCMD INSTANCE = new TutorialCMD();

    private final FancyDialogsPlugin plugin = FancyDialogsPlugin.get();
    private final Translator translator = FancyDialogsPlugin.get().getTranslator();

    private TutorialCMD() {
    }

    @Command("tutorial open <tutorial>")
    @Permission("fancydialogs.commands.tutorial")
    public void onOpen(
            final Player sender,
            final String tutorial
            ) {
        sender.sendMessage("Opening tutorial: " + tutorial);
    }
}
