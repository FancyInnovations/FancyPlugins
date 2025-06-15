package com.fancyinnovations.fancydialogs.fancynpcs;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import com.fancyinnovations.fancydialogs.api.Dialog;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.api.actions.executor.ActionExecutionContext;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OpenDialogNpcAction extends NpcAction {

    public OpenDialogNpcAction() {
        super("open_dialog", true);
    }

    @Override
    public void execute(@NotNull ActionExecutionContext context, @Nullable String value) {
        Dialog dialog = FancyDialogsPlugin.get().getDialogRegistry().get(value);
        if (dialog == null) {
            FancyDialogsPlugin.get().getFancyLogger().warn("Dialog with ID '" + value + "' not found for NPC action 'open_dialog'.");
            return;
        }

        dialog.open(context.getPlayer());
    }

    public void register() {
        if (Bukkit.getPluginManager().isPluginEnabled("FancyNpcs")) {
            FancyNpcsPlugin.get().getActionManager().registerAction(this);
            FancyDialogsPlugin.get().getFancyLogger().info("Registered NPC action 'open_dialog' for FancyNpcs.");
        }
    }
}
