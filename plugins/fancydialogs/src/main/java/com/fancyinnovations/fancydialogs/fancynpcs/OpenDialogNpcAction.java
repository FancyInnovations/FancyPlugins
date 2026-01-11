package com.fancyinnovations.fancydialogs.fancynpcs;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import com.fancyinnovations.fancydialogs.api.Dialog;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.api.actions.executor.ActionExecutionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OpenDialogNpcAction extends NpcAction {

    public OpenDialogNpcAction() {
        super("open_dialog", true);
    }

    @Override
    public void execute(@NotNull ActionExecutionContext context, @Nullable String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        // Parse value as: dialogId [arg1] [arg2] ...
        String[] parts = value.split(" ");
        String dialogId = parts[0];
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);

        Dialog dialog = FancyDialogsPlugin.get().getDialogRegistry().get(dialogId);
        if (dialog == null) {
            FancyDialogsPlugin.get().getFancyLogger().warn("Dialog with ID '" + dialogId + "' not found for NPC action 'open_dialog'.");
            return;
        }

        dialog.open(context.getPlayer(), args);
    }

    public void register() {
        FancyNpcsPlugin.get().getActionManager().registerAction(this);
        FancyDialogsPlugin.get().getFancyLogger().info("Registered NPC action 'open_dialog' for FancyNpcs.");
    }
}
