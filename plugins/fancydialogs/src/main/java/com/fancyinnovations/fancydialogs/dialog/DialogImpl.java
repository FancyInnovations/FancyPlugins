package com.fancyinnovations.fancydialogs.dialog;

import com.fancyinnovations.fancydialogs.api.Dialog;
import com.fancyinnovations.fancydialogs.api.data.types.DialogType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DialogImpl extends Dialog {

    public DialogImpl(@NotNull String id, @NotNull Type type, @NotNull DialogType dialog) {
        super(id, type, dialog);
    }

    @Override
    public void open(Player player) {
        // TODO open dialog packet
    }

    @Override
    public void close(Player player) {
        // TODO close dialog packet
    }

}
