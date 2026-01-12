package com.fancyinnovations.fancydialogs.commands;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import com.fancyinnovations.fancydialogs.api.Dialog;
import de.oliver.fancylib.translations.Translator;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.bukkit.parameters.EntitySelector;

import java.util.Collection;

public final class DialogCMD {

    public static final DialogCMD INSTANCE = new DialogCMD();

    private final FancyDialogsPlugin plugin = FancyDialogsPlugin.get();
    private final Translator translator = FancyDialogsPlugin.get().getTranslator();

    private DialogCMD() {
    }

    @Command("dialog list")
    @Description("Lists all registered dialogs")
    @CommandPermission("fancydialogs.commands.dialog.list")
    public void list(BukkitCommandActor actor) {
        Collection<Dialog> dialogs = plugin.getDialogRegistry().getAll();
        if (dialogs.isEmpty()) {
            translator.translate("commands.dialog.list.empty").send(actor.sender());
            return;
        }

        translator.translate("commands.dialog.list.header")
                .replace("count", String.valueOf(dialogs.size()))
                .send(actor.sender());

        for (Dialog dialog : dialogs) {
            translator.translate("commands.dialog.list.entry")
                    .replace("id", dialog.getId())
                    .replace("title", dialog.getData().title())
                    .send(actor.sender());
        }
    }

    @Command("dialog open <dialog>")
    @Description("Opens a dialog (for a player) by its ID with optional arguments")
    @CommandPermission("fancydialogs.commands.dialog.open")
    public void open(
            BukkitCommandActor actor,
            Dialog dialog,
            @Optional EntitySelector<Player> target,
            @Optional String[] args
    ) {
        String[] dialogArgs = args != null ? args : new String[0];

        if (target == null) {
            if (actor.isPlayer()) {
                dialog.open(actor.asPlayer(), dialogArgs);
                translator.translate("commands.dialog.open.self")
                        .replace("id", dialog.getId())
                        .send(actor.sender());
            } else {
                translator.translate("commands.dialog.open.console_requires_target")
                        .replace("id", dialog.getId())
                        .send(actor.sender());
            }
        } else {
            for (Player player : target) {
                dialog.open(player, dialogArgs);
            }

            Collection<String> players = target.stream()
                    .map(Player::getName)
                    .toList();
            String playersStr = String.join(", ", players);

            translator.translate("commands.dialog.open.other")
                    .replace("id", dialog.getId())
                    .replace("target", playersStr)
                    .send(actor.sender());
        }
    }
}
