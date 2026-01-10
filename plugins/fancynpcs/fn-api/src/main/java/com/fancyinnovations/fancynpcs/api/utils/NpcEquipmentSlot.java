package com.fancyinnovations.fancynpcs.api.utils;

import org.bukkit.entity.EntityType;

import java.util.EnumSet;
import java.util.Set;

public enum NpcEquipmentSlot {
    MAINHAND,
    OFFHAND,
    FEET,
    LEGS,
    CHEST,
    HEAD,
    BODY,
    SADDLE;

    private static final Set<NpcEquipmentSlot> HUMANOID_SLOTS = EnumSet.of(MAINHAND, OFFHAND, HEAD, CHEST, LEGS, FEET);
    private static final Set<NpcEquipmentSlot> HANDS_ONLY = EnumSet.of(MAINHAND, OFFHAND);
    private static final Set<NpcEquipmentSlot> MAINHAND_ONLY = EnumSet.of(MAINHAND);
    private static final Set<NpcEquipmentSlot> BODY_ONLY = EnumSet.of(BODY);
    private static final Set<NpcEquipmentSlot> EMPTY_SLOTS = EnumSet.noneOf(NpcEquipmentSlot.class);

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

    public static Set<NpcEquipmentSlot> getValidSlots(EntityType entityType) {
        if (entityType == null) {
            return EMPTY_SLOTS;
        }

        return switch (entityType) {
            case PLAYER, ZOMBIE, SKELETON, HUSK, STRAY, DROWNED,
                 ZOMBIFIED_PIGLIN, PIGLIN, PIGLIN_BRUTE, WITHER_SKELETON,
                 ZOMBIE_VILLAGER, GIANT, ARMOR_STAND -> HUMANOID_SLOTS;

            case VILLAGER, WANDERING_TRADER, WITCH, EVOKER, VINDICATOR,
                 PILLAGER, ILLUSIONER, VEX, ALLAY -> HANDS_ONLY;

            case ENDERMAN, FOX, DOLPHIN -> MAINHAND_ONLY;

            case HORSE, ZOMBIE_HORSE, SKELETON_HORSE, LLAMA, TRADER_LLAMA, WOLF -> BODY_ONLY;

            case SNOWMAN -> EnumSet.of(HEAD);

            default -> {
                if (entityType.name().equals("MANNEQUIN")) {
                    yield HUMANOID_SLOTS;
                }
                yield EMPTY_SLOTS;
            }
        };
    }

    public static boolean isValidSlot(EntityType entityType, NpcEquipmentSlot slot) {
        return getValidSlots(entityType).contains(slot);
    }

}
