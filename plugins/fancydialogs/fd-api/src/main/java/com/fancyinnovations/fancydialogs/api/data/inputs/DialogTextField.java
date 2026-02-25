package com.fancyinnovations.fancydialogs.api.data.inputs;

import java.util.Map;

public class DialogTextField extends DialogInput {

    private final String placeholder;
    private final int maxLength;
    private final int maxLines;
    private final int width;

    public DialogTextField(String key, String label, int order, String placeholder, int maxLength, int maxLines, Map<String, String> requirements, int width) {
        super(key, label, order, requirements);
        this.placeholder = placeholder;
        this.maxLength = maxLength;
        this.maxLines = maxLines;
        this.width = width;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public int getWidth() { return width; }

    public int getMaxLength() {
        return maxLength;
    }

    public int getMaxLines() {
        return maxLines;
    }
}
