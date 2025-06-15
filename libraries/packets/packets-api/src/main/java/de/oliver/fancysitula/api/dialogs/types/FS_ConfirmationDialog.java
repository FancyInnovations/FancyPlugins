package de.oliver.fancysitula.api.dialogs.types;

import de.oliver.fancysitula.api.dialogs.FS_CommonDialogData;
import de.oliver.fancysitula.api.dialogs.FS_Dialog;
import de.oliver.fancysitula.api.dialogs.actions.FS_DialogActionButton;

public class FS_ConfirmationDialog implements FS_Dialog {

    private FS_CommonDialogData dialogData;
    private FS_DialogActionButton yesButton;
    private FS_DialogActionButton noButton;

    public FS_ConfirmationDialog(FS_CommonDialogData dialogData, FS_DialogActionButton yetButton, FS_DialogActionButton noButton) {
        this.dialogData = dialogData;
        this.yesButton = yetButton;
        this.noButton = noButton;
    }

    public FS_CommonDialogData getDialogData() {
        return dialogData;
    }

    public void setDialogData(FS_CommonDialogData dialogData) {
        this.dialogData = dialogData;
    }

    public FS_DialogActionButton getYesButton() {
        return yesButton;
    }

    public void setYesButton(FS_DialogActionButton yesButton) {
        this.yesButton = yesButton;
    }

    public FS_DialogActionButton getNoButton() {
        return noButton;
    }

    public void setNoButton(FS_DialogActionButton noButton) {
        this.noButton = noButton;
    }
}
