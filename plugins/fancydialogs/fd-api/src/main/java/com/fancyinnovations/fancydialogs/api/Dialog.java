package com.fancyinnovations.fancydialogs.api;

import com.fancyinnovations.fancydialogs.api.data.DialogData;
import org.bukkit.entity.Player;

public abstract class Dialog {

    protected String id;
    protected DialogData data;

    public Dialog(String id, DialogData data) {
        this.id = id;
        this.data = data;
    }

    public Dialog() {
    }

    abstract public void open(Player player);

    abstract public void close(Player player);

    public String getId() {
        return id;
    }

    public DialogData getData() {
        return data;
    }
}
