package com.fancyinnovations.fancydialogs.config;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import de.oliver.fancylib.ConfigHelper;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class FancyDialogsConfig {

    private String language;
    private String welcomeDialogID;
    private String quickActionsDialogID;

    public void load() {
        FancyDialogsPlugin.get().reloadConfig();
        final FileConfiguration config = FancyDialogsPlugin.get().getConfig();

        language = (String) ConfigHelper.getOrDefault(config, "language", "default");
        config.setInlineComments("language", List.of("The language of the plugin."));

        welcomeDialogID = (String) ConfigHelper.getOrDefault(config, "welcome_dialog_id", "welcome-dialog");
        config.setInlineComments("welcome_dialog_id", List.of("The ID of the dialog which will be shown to the player when they join the server for the first time."));

        quickActionsDialogID = (String) ConfigHelper.getOrDefault(config, "quick_actions_dialog_id", "quick-actions-dialog");
        config.setInlineComments("quick_actions_dialog_id", List.of("The ID of the dialog which will be shown to the player when they click on the quick actions key ('G' by default)."));
    }

    public String getLanguage() {
        return language;
    }

    public String getWelcomeDialogID() {
        return welcomeDialogID;
    }

    public String getQuickActionsDialogID() {
        return quickActionsDialogID;
    }
}
