package com.fancyinnovations.fancydialogs.api.data.inputs;

import java.util.List;
import java.util.Map;

public class DialogSelect extends DialogInput {

    private final List<Entry> options;

    public DialogSelect(String key, String label, int order, List<Entry> options, Map<String, String> requirements) {
        super(key, label, order, requirements);
        this.options = options;
    }

    public List<Entry> getOptions() {
        return options;
    }

    public record Entry(
            String value,
            String display,
            boolean initial
    ) {

    }

}
