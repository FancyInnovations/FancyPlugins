package com.fancyinnovations.fancydialogs.api;

import com.fancyinnovations.fancydialogs.api.data.DialogData;
import com.fancyinnovations.fancydialogs.api.data.types.DialogType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class Dialog {

    private String id;
    private Type type;
    private DialogType dialog;

    public Dialog(@NotNull String id, @NotNull Type type, @NotNull DialogType dialog) {
        this.id = id;
        this.type = type;
        this.dialog = dialog;
    }

    public Dialog() {
    }

    abstract public void open(Player player);

    abstract public void close(Player player);

    public @NotNull String getId() {
        return id;
    }

    public @NotNull Type getType() {
        return type;
    }

    public void setType(@NotNull Type type) {
        this.type = type;
    }

    public @NotNull DialogType getDialog() {
        return dialog;
    }

    public void setDialog(@NotNull DialogType dialog) {
        this.dialog = dialog;
    }

    public enum Type {
        NOTICE,
    }
}
