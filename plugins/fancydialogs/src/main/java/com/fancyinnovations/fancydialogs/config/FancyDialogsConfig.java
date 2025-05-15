package com.fancyinnovations.fancydialogs.config;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import de.oliver.fancylib.ConfigHelper;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class FancyDialogsConfig {

    private String welcomeDialogID;

    public void load() {
        FancyDialogsPlugin.get().reloadConfig();
        final FileConfiguration config = FancyDialogsPlugin.get().getConfig();

        welcomeDialogID = (String) ConfigHelper.getOrDefault(config, "welcome_dialog_id", "welcome-dialog");
        config.setInlineComments("welcome_dialog_id", List.of("The ID of the dialog which will be shown to the player when they join the server for the first time."));
    }

    public String getWelcomeDialogID() {
        return welcomeDialogID;
    }
}
