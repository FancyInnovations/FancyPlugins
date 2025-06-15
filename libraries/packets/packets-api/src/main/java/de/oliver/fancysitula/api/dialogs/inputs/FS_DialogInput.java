package de.oliver.fancysitula.api.dialogs.inputs;

public class FS_DialogInput {

    private String key;
    private FS_DialogInputControl control;

    public FS_DialogInput(String key, FS_DialogInputControl control) {
        this.key = key;
        this.control = control;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public FS_DialogInputControl getControl() {
        return control;
    }

    public void setControl(FS_DialogInputControl control) {
        this.control = control;
    }
}
