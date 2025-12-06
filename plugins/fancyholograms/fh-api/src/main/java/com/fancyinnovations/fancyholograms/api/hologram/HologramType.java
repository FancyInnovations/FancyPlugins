package com.fancyinnovations.fancyholograms.api.hologram;

import java.util.Arrays;
import java.util.List;

public enum HologramType {
    TEXT(Arrays.asList("background", "textshadow", "textalignment", "opacity", "seethrough", "setline", "removeline", "addline", "insertbefore", "insertafter", "updatetextinterval")),
    ITEM(List.of("item")),
    BLOCK(Arrays.asList("block", "blockstate"));

    private final List<String> commands;

    HologramType(List<String> commands) {
        this.commands = commands;
    }

    public static HologramType getByName(String name) {
        for (HologramType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }

        return null;
    }

    public List<String> getCommands() {
        return commands;
    }

}
