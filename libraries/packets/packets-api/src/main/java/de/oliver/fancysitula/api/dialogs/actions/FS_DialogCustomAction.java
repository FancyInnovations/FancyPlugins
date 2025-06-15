package de.oliver.fancysitula.api.dialogs.actions;

import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Nullable;

public class FS_DialogCustomAction implements FS_DialogActionButtonAction {

    private String id;
    private @Nullable String additions;

    public FS_DialogCustomAction(String id, String additions) {
        this.id = id;
        this.additions = additions;
    }

    @Subst("")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public @Nullable String getAdditions() {
        return additions;
    }

    public void setAdditions(@Nullable String additions) {
        this.additions = additions;
    }
}
