package com.fancyinnovations.fancydialogs.api.data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DialogButton {

    private final String label;
    private final String tooltip;
    private final List<DialogAction> actions;
    private transient String id;
    private final Map<String, String> requirements;
    private final Integer width;

    public DialogButton(String label, String tooltip, List<DialogAction> actions, Map<String, String> requirements, int width) {
        this.id = UUID.randomUUID().toString();
        this.label = label;
        this.tooltip = tooltip;
        this.actions = actions;
        this.requirements = Map.copyOf(requirements);
        this.width = width;
    }

    public String id() {
        if (id == null || id.isEmpty()) {
            id = UUID.randomUUID().toString();
        }
        return id;
    }

    public String label() {
        return label;
    }

    public String tooltip() {
        return tooltip;
    }

    public Map<String, String> requirements() { return requirements; }

    public List<DialogAction> actions() {
        return actions;
    }

    public Integer width() { return width; }

    public record DialogAction(
            String name,
            String data
    ) {

    }

}
