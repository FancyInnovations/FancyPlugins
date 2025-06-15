package de.oliver.fancysitula.api.dialogs;

import de.oliver.fancysitula.api.dialogs.body.FS_DialogBody;
import de.oliver.fancysitula.api.dialogs.inputs.FS_DialogInput;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FS_CommonDialogData {

    private String title;
    private @Nullable String externalTitle;
    private boolean canCloseWithEscape;
    private boolean pause;
    private FS_DialogAction afterAction;
    private List<FS_DialogBody> body;
    private List<FS_DialogInput> inputs;

    public FS_CommonDialogData(String title, @Nullable String externalTitle, boolean canCloseWithEscape, boolean pause, FS_DialogAction afterAction, List<FS_DialogBody> body, List<FS_DialogInput> inputs) {
        this.title = title;
        this.externalTitle = externalTitle;
        this.canCloseWithEscape = canCloseWithEscape;
        this.pause = pause;
        this.afterAction = afterAction;
        this.body = body;
        this.inputs = inputs;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public @Nullable String getExternalTitle() {
        return externalTitle;
    }

    public void setExternalTitle(@Nullable String externalTitle) {
        this.externalTitle = externalTitle;
    }

    public boolean isCanCloseWithEscape() {
        return canCloseWithEscape;
    }

    public void setCanCloseWithEscape(boolean canCloseWithEscape) {
        this.canCloseWithEscape = canCloseWithEscape;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public FS_DialogAction getAfterAction() {
        return afterAction;
    }

    public void setAfterAction(FS_DialogAction afterAction) {
        this.afterAction = afterAction;
    }

    public List<FS_DialogBody> getBody() {
        return body;
    }

    public void setBody(List<FS_DialogBody> body) {
        this.body = body;
    }

    public List<FS_DialogInput> getInputs() {
        return inputs;
    }

    public void setInputs(List<FS_DialogInput> inputs) {
        this.inputs = inputs;
    }
}
