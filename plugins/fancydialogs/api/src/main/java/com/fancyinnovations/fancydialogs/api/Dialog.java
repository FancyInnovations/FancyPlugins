package com.fancyinnovations.fancydialogs.api;

import com.fancyinnovations.fancydialogs.api.data.types.DialogType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class Dialog {

    private final @NotNull String id;
    private @NotNull DialogType dialog;

    public Dialog(@NotNull String id, @NotNull DialogType dialog) {
        this.id = id;
        this.dialog = dialog;
    }

    abstract public void open(Player player);

    abstract public void close(Player player);

    public @NotNull String getId() {
        return id;
    }

    public @NotNull DialogType getDialog() {
        return dialog;
    }

    public void setDialog(@NotNull DialogType dialog) {
        this.dialog = dialog;
    }
}
