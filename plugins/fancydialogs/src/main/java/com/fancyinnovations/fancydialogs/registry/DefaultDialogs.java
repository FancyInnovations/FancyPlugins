package com.fancyinnovations.fancydialogs.registry;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import com.fancyinnovations.fancydialogs.api.data.DialogBodyData;
import com.fancyinnovations.fancydialogs.api.data.DialogButton;
import com.fancyinnovations.fancydialogs.api.data.DialogData;
import com.fancyinnovations.fancydialogs.api.data.inputs.DialogCheckbox;
import com.fancyinnovations.fancydialogs.api.data.inputs.DialogInputs;
import com.fancyinnovations.fancydialogs.api.data.inputs.DialogSelect;
import com.fancyinnovations.fancydialogs.api.data.inputs.DialogTextField;
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

        welcomeToFancyDialogsDialog();
        welcomeDialog();
        quickActions();
    }

    private static void welcomeToFancyDialogsDialog() {
        DialogData data = new DialogData(
                "welcome_to_fancydialogs",
                "<u><b><color:#ff7300>Welcome to FancyDialogs!</color></b></u>",
                false,
                List.of(
                        new DialogBodyData("<color:#ffd199><i>The simple and lightweight dialog plugin for your server!<i></color>"),
                        new DialogBodyData(""),
                        new DialogBodyData("This dialog is a demonstration of how to use FancyDialogs to create interactive and user-friendly dialogs."),
                        new DialogBodyData("FancyDialogs supports <rainbow>MiniMessages</rainbow> and PlaceholderAPI"),
                        new DialogBodyData("Explore more features in the documentation (<click:open_url:'https://fancyinnovations.com/docs/minecraft-plugins/fancydialogs'><u>click here</u></click>)."),
                        new DialogBodyData("<gradient:#ff7300:#ffd199:#ff7300>Enjoy using FancyDialogs :D</gradient>")
                ),
                new DialogInputs(
                        List.of(
                                new DialogTextField(
                                        "nickname",
                                        "<color:#ff7300>What is your nickname?</color>",
                                        1,
                                        "",
                                        50,
                                        1
                                )
                        ),
                        List.of(
                                new DialogSelect(
                                        "color_choice",
                                        "<color:#ff7300>Choose your favorite color</color>",
                                        2,
                                        List.of(
                                                new DialogSelect.Entry("red", "<color:red>Red</color>", true),
                                                new DialogSelect.Entry("green", "<color:green>Green</color>", false),
                                                new DialogSelect.Entry("blue", "<color:blue>Blue</color>", false),
                                                new DialogSelect.Entry("yellow", "<color:yellow>Yellow</color>", false)
                                        )
                                )
                        ),
                        List.of(
                                new DialogCheckbox(
                                        "is_cool",
                                        "<color:#ff7300>Are you cool?</color>",
                                        3,
                                        true
                                )
                        )
                ),
                List.of(
                        new DialogButton(
                                "<color:#ff4f19>Close</color>",
                                "<color:#ff4f19>Enjoy using FancyDialogs</color>",
                                List.of()
                        ),
                        new DialogButton(
                                "<color:#ffd000>Show favourite color</color>",
                                "<color:#ff4f19>Click to show your fav color :D</color>",
                                List.of(
                                        new DialogButton.DialogAction("message", "Hi {nickname}, your favorite color is: <color:{color_choice}>{color_choice}</color>")
                                )
                        ),
                        new DialogButton(
                                "<color:#ffd000>Check if you are cool</color>",
                                "<color:#ff4f19>Click to know if, you are cool</color>",
                                List.of(
                                        new DialogButton.DialogAction("message", "Hi {nickname}, are you cool: {is_cool}")
                                )
                        )
                )
        );

        register(data);
    }

    private static void welcomeDialog() {
        DialogData data = new DialogData(
                "welcome",
                "<b><color:#00ff5e>Welcome to {server_name}!</color></b>",
                false,
                List.of(
                        new DialogBodyData("<color:#a8ffb4><i>The best Minecraft server on earth!<i></color>"),
                        new DialogBodyData(""),
                        new DialogBodyData("We are glad to have you here!"),
                        new DialogBodyData("If you have any questions, feel free to ask our staff members.")
                ),
                DialogInputs.EMPTY,
                List.of(
                        new DialogButton(
                                "<color:red>Read the rules</color>",
                                "<color:red>Click to read our rules!</color>",
                                List.of(
                                        new DialogButton.DialogAction("open_dialog", "rules")
                                )
                        ),
                        new DialogButton(
                                "<color:#00ff5e>Start playing</color>",
                                "<color:#00ff5e>Click to start playing!</color>",
                                List.of()
                        ),
                        new DialogButton(
                                "<color:#1787ff>Join our Discord</color>",
                                "<color:#1787ff>Click to join our Discord server!</color>",
                                List.of(
                                        new DialogButton.DialogAction("message", "Join our Discord server here: LINK TO DISCORD")
                                )
                        ),
                        new DialogButton(
                                "<color:#ffee00>Visit our website</color>",
                                "<color:#ffee00>Click to visit our website!</color>",
                                List.of(
                                        new DialogButton.DialogAction("message", "Visit our website here: LINK TO WEBSITE")
                                )
                        )
                )
        );

        register(data);
    }

    private static void quickActions() {
        DialogData data = new DialogData(
                "quick_actions",
                "<u><b><color:gold>Quick Actions</color></b></u>",
                false,
                List.of(
                        new DialogBodyData("Here you can quickly access some of the most important features of our server.")
                ),
                DialogInputs.EMPTY,
                List.of(
                        new DialogButton(
                                "<color:#ffee00>Visit our website</color>",
                                "<color:#ffee00>Click to visit our website!</color>",
                                List.of(
                                        new DialogButton.DialogAction("message", "Visit our website here: LINK TO WEBSITE")
                                )
                        ),
                        new DialogButton(
                                "<color:#ffee00>Read the rules</color>",
                                "<color:#ffee00>Click to read our rules!</color>",
                                List.of(
                                        new DialogButton.DialogAction("open_dialog", "rules")
                                )
                        ),
                        new DialogButton(
                                "<color:#ffee00>Join our Discord</color>",
                                "<color:#ffee00>Click to join our Discord server!</color>",
                                List.of(
                                        new DialogButton.DialogAction("message", "Join our Discord server here: LINK TO DISCORD")
                                )
                        ),
                        new DialogButton(
                                "<color:#ffee00>Support us</color>",
                                "<color:#ffee00>Click to support us!</color>",
                                List.of(
                                        new DialogButton.DialogAction("message", "Support us by donating here: LINK TO DONATE")
                                )
                        ),
                        new DialogButton(
                                "<color:red>Close</color>",
                                "<color:red>Click to close this dialog!</color>",
                                List.of()
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
