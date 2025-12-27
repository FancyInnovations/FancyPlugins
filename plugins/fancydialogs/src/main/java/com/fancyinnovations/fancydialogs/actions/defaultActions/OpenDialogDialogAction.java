package com.fancyinnovations.fancydialogs.actions.defaultActions;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import com.fancyinnovations.fancydialogs.api.Dialog;
import com.fancyinnovations.fancydialogs.api.DialogAction;
import org.bukkit.entity.Player;

public class OpenDialogDialogAction implements DialogAction {

    public static final OpenDialogDialogAction INSTANCE = new OpenDialogDialogAction();

    private OpenDialogDialogAction() {
    }

    @Override
    public void execute(Player player, Dialog dialog, String data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        // Parse data as: dialogId [arg1] [arg2] ...
        String[] parts = data.split(" ");
        String dialogId = parts[0];
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);

        Dialog targetDialog = FancyDialogsPlugin.get().getDialogRegistry().get(dialogId);
        if (targetDialog == null) {
            FancyDialogsPlugin.get().getFancyLogger().warn("Dialog with ID '" + dialogId + "' not found.");
            return;
        }

        targetDialog.open(player, args);
    }

}
