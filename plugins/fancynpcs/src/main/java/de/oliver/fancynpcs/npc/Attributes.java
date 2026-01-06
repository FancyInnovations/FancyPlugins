package de.oliver.fancynpcs.npc;

import de.oliver.fancynpcs.api.NpcAttribute;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Defines all available NPC attributes.
 * Attribute application is handled by NpcAttributeHandler via FancySitula packets.
 */
public class Attributes {

    private static final List<String> BOOLEAN_VALUES = List.of("true", "false");
    private static final List<String> DYE_COLORS = List.of(
            "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink",
            "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
    );
    private static final List<String> COLLAR_COLORS = List.of(
            "red", "blue", "yellow", "green", "purple", "orange", "lime", "magenta",
            "brown", "white", "gray", "light_gray", "light_blue", "black", "cyan", "pink", "none"
    );

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        // Entity attributes (all entities)
        List<EntityType> allTypes = Arrays.stream(EntityType.values()).toList();
        attributes.add(attr("on_fire", BOOLEAN_VALUES, allTypes));
        attributes.add(attr("invisible", BOOLEAN_VALUES, allTypes));
        attributes.add(attr("silent", BOOLEAN_VALUES, allTypes));
        attributes.add(attr("shaking", BOOLEAN_VALUES, allTypes));
        attributes.add(attr("on_ground", BOOLEAN_VALUES, allTypes));

        // Ageable mob attributes
        List<EntityType> ageableTypes = Arrays.stream(EntityType.values())
                .filter(type -> type.getEntityClass() != null && Ageable.class.isAssignableFrom(type.getEntityClass()))
                .toList();
        attributes.add(attr("baby", BOOLEAN_VALUES, ageableTypes));

        // Living entity attributes
        List<EntityType> livingTypes = Arrays.stream(EntityType.values())
                .filter(type -> type.getEntityClass() != null && LivingEntity.class.isAssignableFrom(type.getEntityClass()))
                .toList();
        attributes.add(attr("use_item", List.of("main_hand", "off_hand", "none"), livingTypes));

        // Sheep
        attributes.add(attr("wool_color", DYE_COLORS, List.of(EntityType.SHEEP)));
        attributes.add(attr("sheared", BOOLEAN_VALUES, List.of(EntityType.SHEEP)));

        // Slime & Magma Cube
        attributes.add(attr("size", List.of(), List.of(EntityType.SLIME, EntityType.MAGMA_CUBE)));

        // Armor Stand
        attributes.add(attr("show_arms", BOOLEAN_VALUES, List.of(EntityType.ARMOR_STAND)));
        attributes.add(attr("small", BOOLEAN_VALUES, List.of(EntityType.ARMOR_STAND)));
        attributes.add(attr("no_baseplate", BOOLEAN_VALUES, List.of(EntityType.ARMOR_STAND)));
        attributes.add(attr("marker", BOOLEAN_VALUES, List.of(EntityType.ARMOR_STAND)));

        // Wolf
        attributes.add(attr("pose", List.of("standing", "sitting"), List.of(EntityType.WOLF)));
        attributes.add(attr("angry", BOOLEAN_VALUES, List.of(EntityType.WOLF)));
        attributes.add(attr("variant", List.of("pale", "spotted", "snowy", "black", "ashen", "rusty", "woods", "chestnut", "striped"), List.of(EntityType.WOLF)));
        attributes.add(attr("collar_color", COLLAR_COLORS, List.of(EntityType.WOLF)));

        // Cat
        attributes.add(attr("variant", List.of("tabby", "black", "red", "siamese", "british_shorthair", "calico", "persian", "ragdoll", "white", "jellie", "all_black"), List.of(EntityType.CAT)));
        attributes.add(attr("pose", List.of("standing", "sleeping", "sitting"), List.of(EntityType.CAT)));
        attributes.add(attr("collar_color", COLLAR_COLORS, List.of(EntityType.CAT)));
        attributes.add(attr("lying", BOOLEAN_VALUES, List.of(EntityType.CAT)));
        attributes.add(attr("relaxed", BOOLEAN_VALUES, List.of(EntityType.CAT)));

        // Fox
        attributes.add(attr("type", List.of("red", "snow"), List.of(EntityType.FOX)));
        attributes.add(attr("pose", List.of("standing", "sleeping", "sitting", "crouching"), List.of(EntityType.FOX)));

        // Axolotl
        attributes.add(attr("variant", List.of("lucy", "wild", "gold", "cyan", "blue"), List.of(EntityType.AXOLOTL)));
        attributes.add(attr("playing_dead", BOOLEAN_VALUES, List.of(EntityType.AXOLOTL)));

        // Panda
        attributes.add(attr("main_gene", List.of("normal", "lazy", "worried", "playful", "brown", "weak", "aggressive"), List.of(EntityType.PANDA)));
        attributes.add(attr("hidden_gene", List.of("normal", "lazy", "worried", "playful", "brown", "weak", "aggressive"), List.of(EntityType.PANDA)));
        attributes.add(attr("rolling", BOOLEAN_VALUES, List.of(EntityType.PANDA)));
        attributes.add(attr("on_back", BOOLEAN_VALUES, List.of(EntityType.PANDA)));

        // Bee
        attributes.add(attr("has_nectar", BOOLEAN_VALUES, List.of(EntityType.BEE)));
        attributes.add(attr("has_stung", BOOLEAN_VALUES, List.of(EntityType.BEE)));

        // Pig
        attributes.add(attr("saddle", BOOLEAN_VALUES, List.of(EntityType.PIG)));

        // Strider
        attributes.add(attr("saddle", BOOLEAN_VALUES, List.of(EntityType.STRIDER)));

        // Goat
        attributes.add(attr("has_left_horn", BOOLEAN_VALUES, List.of(EntityType.GOAT)));
        attributes.add(attr("has_right_horn", BOOLEAN_VALUES, List.of(EntityType.GOAT)));
        attributes.add(attr("screaming", BOOLEAN_VALUES, List.of(EntityType.GOAT)));

        // Piglin
        attributes.add(attr("dancing", BOOLEAN_VALUES, List.of(EntityType.PIGLIN)));
        attributes.add(attr("immune_to_zombification", BOOLEAN_VALUES, List.of(EntityType.PIGLIN)));

        // Creeper
        attributes.add(attr("powered", BOOLEAN_VALUES, List.of(EntityType.CREEPER)));
        attributes.add(attr("ignited", BOOLEAN_VALUES, List.of(EntityType.CREEPER)));

        // Villager
        attributes.add(attr("profession", List.of("none", "armorer", "butcher", "cartographer", "cleric", "farmer", "fisherman", "fletcher", "leatherworker", "librarian", "mason", "nitwit", "shepherd", "toolsmith", "weaponsmith"), List.of(EntityType.VILLAGER)));
        attributes.add(attr("type", List.of("desert", "jungle", "plains", "savanna", "snow", "swamp", "taiga"), List.of(EntityType.VILLAGER)));

        // Parrot
        attributes.add(attr("variant", List.of("red_blue", "blue", "green", "yellow_blue", "gray"), List.of(EntityType.PARROT)));
        attributes.add(attr("pose", List.of("standing", "sitting"), List.of(EntityType.PARROT)));

        // Rabbit
        attributes.add(attr("variant", List.of("brown", "white", "black", "white_splotched", "gold", "salt", "evil"), List.of(EntityType.RABBIT)));

        // Frog
        attributes.add(attr("variant", List.of("temperate", "warm", "cold"), List.of(EntityType.FROG)));

        // Llama
        attributes.add(attr("variant", List.of("creamy", "white", "brown", "gray"), List.of(EntityType.LLAMA, EntityType.TRADER_LLAMA)));

        // Shulker
        attributes.add(attr("color", DYE_COLORS, List.of(EntityType.SHULKER)));

        // Allay
        attributes.add(attr("dancing", BOOLEAN_VALUES, List.of(EntityType.ALLAY)));

        // Camel
        attributes.add(attr("pose", List.of("standing", "sitting"), List.of(EntityType.CAMEL)));

        // Armadillo
        attributes.add(attr("state", List.of("idle", "rolling", "scared"), List.of(EntityType.ARMADILLO)));

        // Horse variants
        attributes.add(attr("variant", List.of("white", "creamy", "chestnut", "brown", "black", "gray", "dark_brown"), List.of(EntityType.HORSE)));
        attributes.add(attr("marking", List.of("none", "white", "white_field", "white_dots", "black_dots"), List.of(EntityType.HORSE)));

        // Vex
        attributes.add(attr("charging", BOOLEAN_VALUES, List.of(EntityType.VEX)));

        // Tropical Fish
        attributes.add(attr("pattern", List.of("kob", "sunstreak", "snooper", "dasher", "brinely", "spotty", "flopper", "stripey", "glitter", "blockfish", "betty", "clayfish"), List.of(EntityType.TROPICAL_FISH)));
        attributes.add(attr("body_color", DYE_COLORS, List.of(EntityType.TROPICAL_FISH)));
        attributes.add(attr("pattern_color", DYE_COLORS, List.of(EntityType.TROPICAL_FISH)));

        return attributes;
    }

    private static NpcAttribute attr(String name, List<String> values, List<EntityType> types) {
        // No-op apply function - NpcAttributeHandler handles application via FancySitula
        return new NpcAttribute(name, values, types, (npc, value) -> {});
    }
}
