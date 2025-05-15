package com.fancyinnovations.fancydialogs.api.body;

import org.jetbrains.annotations.NotNull;

public class TextBody extends DialogBody {

    private @NotNull String content;
    private int width;

    public TextBody(@NotNull String content, int width) {
        this.content = content;
        this.width = width;
    }

    public @NotNull String getContent() {
        return content;
    }

    public void setContent(@NotNull String content) {
        this.content = content;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
