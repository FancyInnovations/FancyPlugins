package com.fancyinnovations.fancynpcs.api.utils;

public enum NpcEquipmentSlot {
    MAINHAND,
    OFFHAND,
    FEET,
    LEGS,
    CHEST,
    HEAD,
    BODY,    // Used for wolf armor, horse armor, llama carpet, happy ghast harness
    SADDLE;  // Added in 1.21.5

    public static NpcEquipmentSlot parse(String s) {
        for (NpcEquipmentSlot slot : values()) {
            if (slot.name().equalsIgnoreCase(s)) {
                return slot;
            }
        }

        return null;
    }

    public String toNmsName() {
        return name().toLowerCase();
    }

}
