package com.fancyinnovations.fancydialogs.api;

import com.fancyinnovations.fancydialogs.api.data.DialogBodyData;
import com.fancyinnovations.fancydialogs.api.data.DialogButton;
import com.fancyinnovations.fancydialogs.api.data.DialogData;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ConfirmationDialog {

    private final String title;
    private final String question;
    private final String confirmText;
    private final String cancelText;

    private final DialogData dialogData;

    public ConfirmationDialog(String title, String question, String confirmText, String cancelText) {
        this.title = title;
        this.question = question;
        this.confirmText = confirmText;
        this.cancelText = cancelText;

        this.dialogData = new DialogData(
                "confirmation_dialog_" + UUID.randomUUID(),
                this.title,
                this.title,
                false,
                List.of(
                        new DialogBodyData(this.question)
                ),
                List.of(
                        new DialogButton(
                                this.confirmText,
                                this.confirmText,
                                "confirm"
                        ),
                        new DialogButton(
                                this.cancelText,
                                this.cancelText,
                                "cancel"
                        )
                )
        );
    }

    public ConfirmationDialog(String question) {
        this("Confirmation", question, "Confirm", "Cancel");
    }

    public ConfirmationDialog(String title, String question) {
        this(title, question, "Confirm", "Cancel");
    }

    public static boolean ask(Player player, String question) {
        return new ConfirmationDialog(question).ask(player).join();
    }

    public CompletableFuture<Boolean> ask(Player player) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        FancyDialogs.get()
                .createDialog(dialogData)
                .open(player);

        // TODO wait for user response

        future.complete(true);
        return future;
    }

    public String getTitle() {
        return title;
    }

    public String getQuestion() {
        return question;
    }

    public String getConfirmText() {
        return confirmText;
    }

    public String getCancelText() {
        return cancelText;
    }
}
