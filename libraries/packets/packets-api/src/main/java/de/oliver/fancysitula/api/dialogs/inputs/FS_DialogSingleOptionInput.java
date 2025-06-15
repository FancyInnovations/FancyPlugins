package de.oliver.fancysitula.api.dialogs.inputs;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FS_DialogSingleOptionInput implements FS_DialogInputControl {

    private int width;
    private List<Entry> entries;
    private String label;
    private boolean isLabelVisible;

    public FS_DialogSingleOptionInput(int width, List<Entry> entries, String label, boolean isLabelVisible) {
        this.width = width;
        this.entries = entries;
        this.label = label;
        this.isLabelVisible = isLabelVisible;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
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

    public static class Entry {

        private String id;
        private @Nullable String display;
        private boolean initial;

        public Entry(String id, @Nullable String display, boolean initial) {
            this.id = id;
            this.display = display;
            this.initial = initial;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public @Nullable String getDisplay() {
            return display;
        }

        public void setDisplay(@Nullable String display) {
            this.display = display;
        }

        public boolean isInitial() {
            return initial;
        }

        public void setInitial(boolean initial) {
            this.initial = initial;
        }
    }

}
