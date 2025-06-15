package com.fancyinnovations.fancydialogs.dialog;

import com.fancyinnovations.fancydialogs.api.Dialog;
import com.fancyinnovations.fancydialogs.api.data.DialogBodyData;
import com.fancyinnovations.fancydialogs.api.data.DialogButton;
import com.fancyinnovations.fancydialogs.api.data.DialogData;
import de.oliver.fancysitula.api.dialogs.FS_CommonDialogData;
import de.oliver.fancysitula.api.dialogs.FS_DialogAction;
import de.oliver.fancysitula.api.dialogs.actions.FS_CommonButtonData;
import de.oliver.fancysitula.api.dialogs.actions.FS_DialogActionButton;
import de.oliver.fancysitula.api.dialogs.actions.FS_DialogCustomAction;
import de.oliver.fancysitula.api.dialogs.body.FS_DialogBody;
import de.oliver.fancysitula.api.dialogs.body.FS_DialogTextBody;
import de.oliver.fancysitula.api.dialogs.types.FS_MultiActionDialog;
import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.factories.FancySitula;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DialogImpl extends Dialog {

    private FS_MultiActionDialog fsDialog;

    public DialogImpl(String id, DialogData data) {
        super(id, data);
        init();
    }

    private void init() {
        List<FS_DialogBody> body = new ArrayList<>();
        for (DialogBodyData bodyData : data.body()) {
            FS_DialogTextBody fsDialogTextBody = new FS_DialogTextBody(
                    bodyData.text(),
                    200 // default text width
            );
            body.add(fsDialogTextBody);
        }

        List<FS_DialogActionButton> actions = new ArrayList<>();
        for (DialogButton button : data.buttons()) {
            FS_DialogActionButton fsDialogActionButton = new FS_DialogActionButton(
                    new FS_CommonButtonData(
                            button.label(),
                            button.tooltip(),
                            150 // default button width
                    ),
                    new FS_DialogCustomAction("fancydialogs_dialog_action", button.action())
            );
            actions.add(fsDialogActionButton);
        }

        this.fsDialog = new FS_MultiActionDialog(
                new FS_CommonDialogData(
                        data.title(),
                        data.externalTitle(),
                        data.canCloseWithEscape(),
                        false,
                        FS_DialogAction.CLOSE,
                        body,
                        new ArrayList<>() // inputs
                ),
                actions, // actions
                null,
                2
        );
    }

    @Override
    public void open(Player player) {
        FancySitula.PACKET_FACTORY
                .createShowDialogPacket(fsDialog)
                .send(new FS_RealPlayer(player));
    }

    @Override
    public void close(Player player) {
        FancySitula.PACKET_FACTORY
                .createClearDialogPacket()
                .send(new FS_RealPlayer(player));
    }

}
