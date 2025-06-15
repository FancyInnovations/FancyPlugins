package com.fancyinnovations.fancydialogs.api;

import com.fancyinnovations.fancydialogs.api.data.DialogData;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public interface FancyDialogs {

    static FancyDialogs get() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("FancyDialogs");
        return (FancyDialogs) plugin;
    }

    Dialog createDialog(DialogData data);

}
