package de.oliver.fancysitula.api.dialogs.actions;

public class FS_DialogActionButton {

    private FS_CommonButtonData buttonData;
    private FS_DialogActionButtonAction action;

    public FS_DialogActionButton(FS_CommonButtonData buttonData, FS_DialogActionButtonAction action) {
        this.buttonData = buttonData;
        this.action = action;
    }

    public FS_CommonButtonData getButtonData() {
        return buttonData;
    }

    public void setButtonData(FS_CommonButtonData buttonData) {
        this.buttonData = buttonData;
    }

    public FS_DialogActionButtonAction getAction() {
        return action;
    }

    public void setAction(FS_DialogActionButtonAction action) {
        this.action = action;
    }
}
