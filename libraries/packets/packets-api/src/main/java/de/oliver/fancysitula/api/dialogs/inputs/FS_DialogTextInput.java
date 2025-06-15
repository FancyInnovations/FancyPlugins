package de.oliver.fancysitula.api.dialogs.inputs;

import org.jetbrains.annotations.Nullable;

public class FS_DialogTextInput implements FS_DialogInputControl {

    private int width;
    private String label;
    private boolean isLabelVisible;
    private String initial;
    private int maxLength;
    private @Nullable MultilineOptions multilineOptions;

    public FS_DialogTextInput(int width, String label, boolean isLabelVisible, String initial, int maxLength, @Nullable MultilineOptions multilineOptions) {
        this.width = width;
        this.label = label;
        this.isLabelVisible = isLabelVisible;
        this.initial = initial;
        this.maxLength = maxLength;
        this.multilineOptions = multilineOptions;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isLabelVisible() {
        return isLabelVisible;
    }

    public void setLabelVisible(boolean labelVisible) {
        isLabelVisible = labelVisible;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public @Nullable MultilineOptions getMultilineOptions() {
        return multilineOptions;
    }

    public void setMultilineOptions(@Nullable MultilineOptions multilineOptions) {
        this.multilineOptions = multilineOptions;
    }

    public static class MultilineOptions {

        private @Nullable Integer maxLines;
        private @Nullable Integer height;

        public MultilineOptions(@Nullable Integer maxLines, @Nullable Integer height) {
            this.maxLines = maxLines;
            this.height = height;
        }

        public @Nullable Integer getMaxLines() {
            return maxLines;
        }

        public void setMaxLines(@Nullable Integer maxLines) {
            this.maxLines = maxLines;
        }

        public @Nullable Integer getHeight() {
            return height;
        }

        public void setHeight(@Nullable Integer height) {
            this.height = height;
        }
    }

}
