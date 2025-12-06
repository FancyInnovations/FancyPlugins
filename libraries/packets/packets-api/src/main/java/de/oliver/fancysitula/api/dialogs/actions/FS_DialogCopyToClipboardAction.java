package de.oliver.fancysitula.api.dialogs.actions;

public class FS_DialogCopyToClipboardAction implements FS_DialogActionButtonAction {

    private String value;

    public FS_DialogCopyToClipboardAction(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}