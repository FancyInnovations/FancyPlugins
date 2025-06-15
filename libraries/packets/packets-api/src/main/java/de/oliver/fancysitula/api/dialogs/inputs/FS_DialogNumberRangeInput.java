package de.oliver.fancysitula.api.dialogs.inputs;

import org.jetbrains.annotations.Nullable;

public class FS_DialogNumberRangeInput implements FS_DialogInputControl {

    private int width;
    private String label;
    private String labelFormat;
    private float start;
    private float end;
    private @Nullable Float initial;
    private @Nullable Float step;

    public FS_DialogNumberRangeInput(int width, String label, String labelFormat, float start, float end, @Nullable Float initial, @Nullable Float step) {
        this.width = width;
        this.label = label;
        this.labelFormat = labelFormat;
        this.start = start;
        this.end = end;
        this.initial = initial;
        this.step = step;
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

    public String getLabelFormat() {
        return labelFormat;
    }

    public void setLabelFormat(String labelFormat) {
        this.labelFormat = labelFormat;
    }

    public float getStart() {
        return start;
    }

    public void setStart(float start) {
        this.start = start;
    }

    public float getEnd() {
        return end;
    }

    public void setEnd(float end) {
        this.end = end;
    }

    public @Nullable Float getInitial() {
        return initial;
    }

    public void setInitial(@Nullable Float initial) {
        this.initial = initial;
    }

    public @Nullable Float getStep() {
        return step;
    }

    public void setStep(@Nullable Float step) {
        this.step = step;
    }
}
