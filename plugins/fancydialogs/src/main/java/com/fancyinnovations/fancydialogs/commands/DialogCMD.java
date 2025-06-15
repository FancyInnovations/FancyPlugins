package com.fancyinnovations.fancydialogs.commands;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import com.fancyinnovations.fancydialogs.api.Dialog;
import de.oliver.fancylib.translations.Translator;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class DialogCMD {

    public static final DialogCMD INSTANCE = new DialogCMD();

    private final FancyDialogsPlugin plugin = FancyDialogsPlugin.get();
    private final Translator translator = FancyDialogsPlugin.get().getTranslator();

    private DialogCMD() {
    }

    @Command("dialog open <dialog>")
    @Description("Opens a dialog (for a player) by its ID")
    @CommandPermission("fancydialogs.commands.registry.unregister")
    public void open(
            Player actor,
            Dialog dialog,
            @Optional Player target
    ) {
        if (target == null) {
            dialog.open(actor);
            translator.translate("commands.dialog.open.self")
                    .replace("id", dialog.getId())
                    .send(actor);
        } else {
            dialog.open(target);
            translator.translate("commands.dialog.open.other")
                    .replace("id", dialog.getId())
                    .replace("target", target.getName())
                    .send(actor);
        }
    }
}
