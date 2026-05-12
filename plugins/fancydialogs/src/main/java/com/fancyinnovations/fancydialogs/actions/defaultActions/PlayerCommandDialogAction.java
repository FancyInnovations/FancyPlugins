package com.fancyinnovations.fancydialogs.actions.defaultActions;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import com.fancyinnovations.fancydialogs.api.Dialog;
import com.fancyinnovations.fancydialogs.api.DialogAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.lushplugins.chatcolorhandler.ChatColorHandler;
import org.lushplugins.chatcolorhandler.parsers.ParserTypes;

public class PlayerCommandDialogAction implements DialogAction {

    public static final PlayerCommandDialogAction INSTANCE = new PlayerCommandDialogAction();

    private PlayerCommandDialogAction() {
    }

    @Override
    public void execute(Player player, Dialog dialog, String data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        String command = ChatColorHandler.translate(data, player, ParserTypes.placeholder());

        Bukkit.getScheduler().runTask(FancyDialogsPlugin.get(), () -> {
            player.chat("/" + command);
        });
    }

}
