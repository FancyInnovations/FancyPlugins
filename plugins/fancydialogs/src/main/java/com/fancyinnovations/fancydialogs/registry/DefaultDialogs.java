package com.fancyinnovations.fancydialogs.registry;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import com.fancyinnovations.fancydialogs.api.data.DialogBodyData;
import com.fancyinnovations.fancydialogs.api.data.DialogButton;
import com.fancyinnovations.fancydialogs.api.data.DialogData;
import com.fancyinnovations.fancydialogs.dialog.DialogImpl;
import com.fancyinnovations.fancydialogs.storage.DialogStorage;

import java.io.File;
import java.util.List;

public class DefaultDialogs {

    private static final DialogRegistry registry = FancyDialogsPlugin.get().getDialogRegistry();
    private static final DialogStorage storage = FancyDialogsPlugin.get().getDialogStorage();

    public static void registerDefaultDialogs() {
        File dialogsFolder = new File("plugins/FancyDialogs/data/dialogs");
        if (dialogsFolder.exists()) {
            return;
        }
        dialogsFolder.mkdirs();

        welcomeDialog();
    }

    private static void welcomeDialog() {
        DialogData data = new DialogData(
                "welcome_to_fancydialogs_dialog",
                "Welcome to FancyDialogs",
                "Welcome to FancyDialogs",
                false,
                List.of(
                        new DialogBodyData("Welcome to FancyDialogs! This is a sample dialog to get you started.")
                ),
                List.of(
                        new DialogButton(
                                "Close",
                                "Close the dialog",
                                "close_dialog"
                        )
                )
        );

        register(data);
    }

    private static void register(DialogData dialogData) {
        DialogImpl dialog = new DialogImpl(dialogData.id(), dialogData);
        storage.save(dialogData);
        registry.register(dialog);
    }

}
