package com.fancyinnovations.fancyholograms.api.hologram;

import java.util.Arrays;
import java.util.List;

public enum HologramType {
    TEXT(Arrays.asList("background", "textshadow", "textalignment", "seethrough", "setline", "removeline", "addline", "insertbefore", "insertafter", "swapLines", "moveLineUp", "moveLineDown", "updatetextinterval")),
    ITEM(List.of("item")),
    BLOCK(List.of("block"));

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
