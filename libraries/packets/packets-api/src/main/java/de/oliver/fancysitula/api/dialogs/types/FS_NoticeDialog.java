package de.oliver.fancysitula.api.dialogs.types;

import de.oliver.fancysitula.api.dialogs.FS_CommonDialogData;
import de.oliver.fancysitula.api.dialogs.FS_Dialog;
import de.oliver.fancysitula.api.dialogs.actions.FS_DialogActionButton;

public class FS_NoticeDialog implements FS_Dialog {

    private FS_CommonDialogData dialogData;
    private FS_DialogActionButton actionButton;

    public FS_NoticeDialog(FS_CommonDialogData dialogData, FS_DialogActionButton actionButton) {
        this.dialogData = dialogData;
        this.actionButton = actionButton;
    }

    public FS_CommonDialogData getDialogData() {
        return dialogData;
    }

    public void setDialogData(FS_CommonDialogData dialogData) {
        this.dialogData = dialogData;
    }

    public FS_DialogActionButton getActionButton() {
        return actionButton;
    }

    public void setActionButton(FS_DialogActionButton actionButton) {
        this.actionButton = actionButton;
    }
}
