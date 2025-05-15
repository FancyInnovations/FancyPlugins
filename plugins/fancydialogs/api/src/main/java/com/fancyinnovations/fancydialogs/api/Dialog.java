package com.fancyinnovations.fancydialogs.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Dialog {

    private final @NotNull String id;
    private @NotNull String title;
    private @Nullable String externalTitle;
    private boolean canCloseWithEscape;

    /**
     * @param externalTitle if null, the title will be used
     * @param canCloseWithEscape default is true
     */
    public Dialog(@NotNull String id, @NotNull String title, @Nullable String externalTitle, @Nullable Boolean canCloseWithEscape) {
        this.id = id;
        this.title = title;
        this.externalTitle = externalTitle == null ? title : externalTitle;
        this.canCloseWithEscape = canCloseWithEscape == null || canCloseWithEscape;
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
}
