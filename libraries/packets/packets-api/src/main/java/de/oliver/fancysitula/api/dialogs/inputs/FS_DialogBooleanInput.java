package de.oliver.fancysitula.api.dialogs.inputs;

public class FS_DialogBooleanInput implements FS_DialogInputControl {

    private String label;
    private boolean initial;
    private String onTrue;
    private String onFalse;

    public FS_DialogBooleanInput(String label, boolean initial, String onTrue, String onFalse) {
        this.label = label;
        this.initial = initial;
        this.onTrue = onTrue;
        this.onFalse = onFalse;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isInitial() {
        return initial;
    }

    public void setInitial(boolean initial) {
        this.initial = initial;
    }

    public String getOnTrue() {
        return onTrue;
    }

    public void setOnTrue(String onTrue) {
        this.onTrue = onTrue;
    }

    public String getOnFalse() {
        return onFalse;
    }

    public void setOnFalse(String onFalse) {
        this.onFalse = onFalse;
    }
}
