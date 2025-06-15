package de.oliver.fancysitula.api.dialogs.actions;

public class FS_DialogRunCommandAction implements FS_DialogActionButtonAction {

    private String command;

    public FS_DialogRunCommandAction(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
