package com.fancyinnovations.fancydialogs.api.data.inputs;

import java.util.Map;

public class DialogCheckbox extends DialogInput {

    private final boolean initial;

    public DialogCheckbox(String key, String label, int order, boolean initial, Map<String, String> requirements) {
        super(key, label, order, requirements);
        this.initial = initial;
    }

    public boolean isInitial() {
        return initial;
    }
}
