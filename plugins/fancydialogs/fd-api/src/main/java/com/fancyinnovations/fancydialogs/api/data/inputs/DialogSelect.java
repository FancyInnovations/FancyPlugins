package com.fancyinnovations.fancydialogs.api.data.inputs;

import java.util.List;
import java.util.Map;

public class DialogSelect extends DialogInput {

    private final List<Entry> options;
    private final Integer width;

    public DialogSelect(String key, String label, int order, List<Entry> options, Map<String, String> requirements, Integer width) {
        super(key, label, order, requirements);
        this.options = options;
        this.width = width;
    }

    public List<Entry> getOptions() {
        return options;
    }

    public Integer getWidth() { return width; }

    public record Entry(
            String value,
            String display,
            boolean initial
    ) {

    }

}
