package com.fancyinnovations.fancydialogs.config;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import de.oliver.fancylib.ConfigHelper;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class FancyDialogsConfig {

    private String language;
    private String welcomeDialogID;

    public void load() {
        FancyDialogsPlugin.get().reloadConfig();
        final FileConfiguration config = FancyDialogsPlugin.get().getConfig();

        language = (String) ConfigHelper.getOrDefault(config, "language", "default");
        config.setInlineComments("language", List.of("The language of the plugin."));

        welcomeDialogID = (String) ConfigHelper.getOrDefault(config, "welcome_dialog_id", "welcome-dialog");
        config.setInlineComments("welcome_dialog_id", List.of("The ID of the dialog which will be shown to the player when they join the server for the first time."));
    }

    public String getWelcomeDialogID() {
        return welcomeDialogID;
    }

    public String getLanguage() {
        return language;
    }
}
