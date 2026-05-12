package com.fancyinnovations.fancydialogs.actions.defaultActions;

import com.fancyinnovations.fancydialogs.api.Dialog;
import com.fancyinnovations.fancydialogs.api.DialogAction;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.lushplugins.chatcolorhandler.ModernChatColorHandler;

public class MessageDialogAction implements DialogAction {

    public static final MessageDialogAction INSTANCE = new MessageDialogAction();

    private MessageDialogAction() {
    }

    @Override
    public void execute(Player player, Dialog dialog, String data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        String raw = data;
        if (player != null) {
            raw = data.replace("{player}", player.getName());
        }

        Component msg = ModernChatColorHandler.translate(raw);
        player.sendMessage(msg);
    }

}
