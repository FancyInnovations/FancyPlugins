package com.fancyinnovations.fancydialogs.commands;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import com.fancyinnovations.fancydialogs.api.Dialog;
import de.oliver.fancylib.translations.Translator;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class QuickActionsCMD {

    public static final QuickActionsCMD INSTANCE = new QuickActionsCMD();

    private final FancyDialogsPlugin plugin = FancyDialogsPlugin.get();
    private final Translator translator = FancyDialogsPlugin.get().getTranslator();

    private QuickActionsCMD() {
    }

    @Command({"quickactions", "qa"})
    @Description("Opens the quick actions menu")
    @CommandPermission("fancydialogs.commands.quickactions")
    public void onQuickActionsOpen(
            final Player actor
    ) {
        String dialogID = plugin.getFancyDialogsConfig().getQuickActionsDialogID();
        Dialog dialog = plugin.getDialogRegistry().get(dialogID);
        if (dialog == null) {
            plugin.getFancyLogger().error("Quick Actions dialog with ID '" + dialogID + "' not found.");
            translator.translate("dialog.not_found")
                    .withPrefix()
                    .replace("id", dialogID)
                    .send(actor);
            return;
        }

        dialog.open(actor);
    }
}
