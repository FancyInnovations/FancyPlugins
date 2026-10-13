package de.oliver.fancysitula.api.utils;

import org.bukkit.inventory.EquipmentSlot;

public enum FS_EquipmentSlot {
    MAINHAND,
    OFFHAND,
    FEET,
    LEGS,
    CHEST,
    HEAD,
    BODY,
    SADDLE, // Added in 1.21.5
    ;

    public static FS_EquipmentSlot fromBukkit(EquipmentSlot equipmentSlot) {
        return switch (equipmentSlot) {
            case HAND -> MAINHAND;
            case OFF_HAND -> OFFHAND;
            case FEET -> FEET;
            case LEGS -> LEGS;
            case CHEST -> CHEST;
            case HEAD -> HEAD;
            case BODY -> BODY;
            case SADDLE -> SADDLE;
        };
    }
}
