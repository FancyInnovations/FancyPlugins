package com.fancyinnovations.fancydialogs.api.dialogs;

import com.fancyinnovations.fancydialogs.api.FancyDialogs;
import com.fancyinnovations.fancydialogs.api.data.DialogBodyData;
import com.fancyinnovations.fancydialogs.api.data.DialogData;
import com.fancyinnovations.fancydialogs.api.data.inputs.DialogInputs;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class NoticeDialog {

    private final String title;
    private final String text;

    private final DialogData dialogData;

    public NoticeDialog(String title, String text) {
        this.title = title;
        this.text = text;

        this.dialogData = new DialogData(
                "notice_dialog_" + UUID.randomUUID(),
                this.title,
                true,
                List.of(
                        new DialogBodyData(this.text)
                ),
                DialogInputs.EMPTY,
                List.of(),
                null,
                0
        );
    }

    public NoticeDialog(String text) {
        this("Notice", text);
    }

    public static void show(Player player, String text) {
        new NoticeDialog(text).show(player);
    }

    public void show(Player player) {
        FancyDialogs.get()
                .createDialog(dialogData)
                .open(player);
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
