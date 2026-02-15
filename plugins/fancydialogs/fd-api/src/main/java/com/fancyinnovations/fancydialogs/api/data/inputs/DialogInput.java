package com.fancyinnovations.fancydialogs.api.data.inputs;

import java.util.Map;

public abstract class DialogInput {

    protected final String key;
    protected final String label;
    protected final int order;
    protected final Map<String, String> requirements;

    public DialogInput(String key, String label, int order, Map<String, String> requirements) {
        this.key = key;
        this.label = label;
        this.order = order;
        this.requirements = requirements;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public int getOrder() {
        return order;
    }

    public Map<String, String> getRequirements() { return requirements; }
}
