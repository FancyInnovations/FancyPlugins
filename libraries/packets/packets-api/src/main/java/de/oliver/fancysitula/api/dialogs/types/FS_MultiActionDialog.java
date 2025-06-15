package de.oliver.fancysitula.api.dialogs.types;

import de.oliver.fancysitula.api.dialogs.FS_CommonDialogData;
import de.oliver.fancysitula.api.dialogs.FS_Dialog;
import de.oliver.fancysitula.api.dialogs.actions.FS_DialogActionButton;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FS_MultiActionDialog implements FS_Dialog {

    private FS_CommonDialogData dialogData;
    private List<FS_DialogActionButton> actions;
    private @Nullable FS_DialogActionButton exitAction;
    private int columns;

    public FS_MultiActionDialog(FS_CommonDialogData dialogData, List<FS_DialogActionButton> actions, @Nullable FS_DialogActionButton exitAction, int columns) {
        this.dialogData = dialogData;
        this.actions = actions;
        this.exitAction = exitAction;
        this.columns = columns;
    }

    public FS_CommonDialogData getDialogData() {
        return dialogData;
    }

    public void setDialogData(FS_CommonDialogData dialogData) {
        this.dialogData = dialogData;
    }

    public List<FS_DialogActionButton> getActions() {
        return actions;
    }

    public void setActions(List<FS_DialogActionButton> actions) {
        this.actions = actions;
    }

    public @Nullable FS_DialogActionButton getExitAction() {
        return exitAction;
    }

    public void setExitAction(@Nullable FS_DialogActionButton exitAction) {
        this.exitAction = exitAction;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }
}
