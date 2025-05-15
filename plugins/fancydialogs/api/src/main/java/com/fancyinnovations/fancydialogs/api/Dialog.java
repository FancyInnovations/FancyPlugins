package com.fancyinnovations.fancydialogs.api;

import com.fancyinnovations.fancydialogs.api.body.DialogBody;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class Dialog {

    private final @NotNull String id;
    private @NotNull String title;
    private @Nullable String externalTitle;
    private boolean canCloseWithEscape;
    private @NotNull List<DialogBody> body;

    /**
     * @param externalTitle if null, the title will be used
     * @param canCloseWithEscape default is true
     */
    public Dialog(@NotNull String id, @NotNull String title, @Nullable String externalTitle, @Nullable Boolean canCloseWithEscape, @NotNull List<DialogBody> body) {
        this.id = id;
        this.title = title;
        this.externalTitle = externalTitle == null ? title : externalTitle;
        this.canCloseWithEscape = canCloseWithEscape == null || canCloseWithEscape;
        this.body = body;
    }

    abstract public void show(Player player);

    abstract public void close(Player player);

    public @NotNull String getId() {
        return id;
    }

    public @NotNull String getTitle() {
        return title;
    }

    public void setTitle(@NotNull String title) {
        this.title = title;
    }

    public @Nullable String getExternalTitle() {
        return externalTitle;
    }

    public void setExternalTitle(@Nullable String externalTitle) {
        this.externalTitle = externalTitle;
    }

    public boolean canCloseWithEscape() {
        return canCloseWithEscape;
    }

    public void setCanCloseWithEscape(boolean canCloseWithEscape) {
        this.canCloseWithEscape = canCloseWithEscape;
    }

    public @NotNull List<DialogBody> getBody() {
        return body;
    }

    public void setBody(@NotNull List<DialogBody> body) {
        this.body = body;
    }
}
