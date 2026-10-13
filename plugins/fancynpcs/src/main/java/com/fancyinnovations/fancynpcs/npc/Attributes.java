package com.fancyinnovations.fancynpcs.npc;

import com.fancyinnovations.fancynpcs.api.NpcAttribute;
import de.oliver.fancysitula.api.utils.ServerVersion;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
        attributes.add(attr("interested", BOOLEAN_VALUES, List.of(EntityType.WOLF))); // Head tilted/begging
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
        attributes.add(attr("pose", List.of("standing", "sitting", "rolling", "on_back", "sneezing"), List.of(EntityType.PANDA)));

        // Bee
        attributes.add(attr("has_nectar", BOOLEAN_VALUES, List.of(EntityType.BEE)));
        attributes.add(attr("has_stung", BOOLEAN_VALUES, List.of(EntityType.BEE)));
        attributes.add(attr("angry", BOOLEAN_VALUES, List.of(EntityType.BEE)));

        // Pig
        attributes.add(attr("saddle", BOOLEAN_VALUES, List.of(EntityType.PIG)));

        // Strider
        attributes.add(attr("saddle", BOOLEAN_VALUES, List.of(EntityType.STRIDER)));
        attributes.add(attr("cold", BOOLEAN_VALUES, List.of(EntityType.STRIDER))); // Shaking/suffocating when not in lava

        // Goat
        attributes.add(attr("has_left_horn", BOOLEAN_VALUES, List.of(EntityType.GOAT)));
        attributes.add(attr("has_right_horn", BOOLEAN_VALUES, List.of(EntityType.GOAT)));
        attributes.add(attr("screaming", BOOLEAN_VALUES, List.of(EntityType.GOAT)));

        // Piglin
        attributes.add(attr("dancing", BOOLEAN_VALUES, List.of(EntityType.PIGLIN)));
        attributes.add(attr("immune_to_zombification", BOOLEAN_VALUES, List.of(EntityType.PIGLIN, EntityType.PIGLIN_BRUTE)));
        attributes.add(attr("charging_crossbow", BOOLEAN_VALUES, List.of(EntityType.PIGLIN)));

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
        attributes.add(attr("dashing", BOOLEAN_VALUES, List.of(EntityType.CAMEL)));

        // Armadillo
        attributes.add(attr("state", List.of("idle", "rolling", "scared", "unrolling"), List.of(EntityType.ARMADILLO)));

        // Horse variants
        attributes.add(attr("variant", List.of("white", "creamy", "chestnut", "brown", "black", "gray", "dark_brown"), List.of(EntityType.HORSE)));
        attributes.add(attr("marking", List.of("none", "white", "white_field", "white_dots", "black_dots"), List.of(EntityType.HORSE)));

        // AbstractHorse saddle (Horse, Donkey, Mule, Camel, Skeleton Horse, Zombie Horse)
        attributes.add(attr("saddle", BOOLEAN_VALUES, List.of(EntityType.HORSE, EntityType.DONKEY, EntityType.MULE, EntityType.CAMEL, EntityType.SKELETON_HORSE, EntityType.ZOMBIE_HORSE)));

        // Cow/Chicken/Pig variants (1.21.5+ - registry-based)
        Set<ServerVersion> v1_21_5_plus = Set.of(
                ServerVersion.v1_21_5, ServerVersion.v1_21_6, ServerVersion.v1_21_7,
                ServerVersion.v1_21_8, ServerVersion.v1_21_9, ServerVersion.v1_21_10, ServerVersion.v1_21_11
        );
        if (v1_21_5_plus.contains(ServerVersion.getCurrentVersion())) {
            attributes.add(attr("variant", List.of("temperate", "cold", "warm"), List.of(EntityType.COW)));
            attributes.add(attr("variant", List.of("temperate", "cold", "warm"), List.of(EntityType.CHICKEN)));
            attributes.add(attr("variant", List.of("temperate", "cold", "warm"), List.of(EntityType.PIG)));
        }

        // Sniffer state
        attributes.add(attr("state", List.of("idling", "feeling_happy", "scenting", "sniffing", "searching", "digging", "rising"), List.of(EntityType.SNIFFER)));

        // Zombie Villager (same profession/type as Villager)
        attributes.add(attr("profession", List.of("none", "armorer", "butcher", "cartographer", "cleric", "farmer", "fisherman", "fletcher", "leatherworker", "librarian", "mason", "nitwit", "shepherd", "toolsmith", "weaponsmith"), List.of(EntityType.ZOMBIE_VILLAGER)));
        attributes.add(attr("type", List.of("desert", "jungle", "plains", "savanna", "snow", "swamp", "taiga"), List.of(EntityType.ZOMBIE_VILLAGER)));

        // Vex
        attributes.add(attr("charging", BOOLEAN_VALUES, List.of(EntityType.VEX)));

        // Raiders (Illagers) - celebrating pose
        attributes.add(attr("celebrating", BOOLEAN_VALUES, List.of(
                EntityType.PILLAGER, EntityType.VINDICATOR, EntityType.EVOKER,
                EntityType.RAVAGER, EntityType.WITCH, EntityType.ILLUSIONER)));

        // Spellcasters (Evoker, Illusioner) - spell casting animation
        attributes.add(attr("spell", List.of("none", "summon_vex", "fangs", "wololo", "disappear", "blindness"),
                List.of(EntityType.EVOKER, EntityType.ILLUSIONER)));

        // Mooshroom (Mushroom Cow)
        attributes.add(attr("variant", List.of("red", "brown"), List.of(EntityType.MOOSHROOM)));

        // Phantom
        attributes.add(attr("size", List.of(), List.of(EntityType.PHANTOM)));

        // Tropical Fish
        attributes.add(attr("pattern", List.of("kob", "sunstreak", "snooper", "dasher", "brinely", "spotty", "flopper", "stripey", "glitter", "blockfish", "betty", "clayfish"), List.of(EntityType.TROPICAL_FISH)));
        attributes.add(attr("body_color", DYE_COLORS, List.of(EntityType.TROPICAL_FISH)));
        attributes.add(attr("pattern_color", DYE_COLORS, List.of(EntityType.TROPICAL_FISH)));

        // ========== NEW ENTITY ATTRIBUTES ==========

        // Snow Golem - pumpkin head
        attributes.add(attr("pumpkin", BOOLEAN_VALUES, List.of(EntityType.SNOW_GOLEM)));

        // Bat - resting/hanging
        attributes.add(attr("resting", BOOLEAN_VALUES, List.of(EntityType.BAT)));

        // Polar Bear - standing/rearing (aggressive pose)
        attributes.add(attr("standing", BOOLEAN_VALUES, List.of(EntityType.POLAR_BEAR)));

        // Pufferfish - inflation state
        attributes.add(attr("puff_state", List.of("small", "medium", "full"), List.of(EntityType.PUFFERFISH)));

        // Salmon - size variant (1.21.4+)
        attributes.add(attr("salmon_size", List.of("small", "medium", "large"), List.of(EntityType.SALMON)));

        // Enderman - creepy/aggressive state
        attributes.add(attr("creepy", BOOLEAN_VALUES, List.of(EntityType.ENDERMAN)));

        // Ghast - charging fireball
        attributes.add(attr("charging", BOOLEAN_VALUES, List.of(EntityType.GHAST)));

        // Guardian/Elder Guardian - moving state (spikes)
        attributes.add(attr("moving", BOOLEAN_VALUES, List.of(EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN)));

        // Warden - anger level
        attributes.add(attr("anger_level", List.of("calm", "agitated", "angry", "max"), List.of(EntityType.WARDEN)));

        // Turtle - laying animation
        attributes.add(attr("laying_egg", BOOLEAN_VALUES, List.of(EntityType.TURTLE)));

        // Witch - drinking potion
        attributes.add(attr("drinking", BOOLEAN_VALUES, List.of(EntityType.WITCH)));

        // Zombie/Husk - drowned conversion (shaking)
        attributes.add(attr("converting", BOOLEAN_VALUES, List.of(EntityType.ZOMBIE, EntityType.HUSK)));

        // Skeleton - stray conversion (freezing/shaking)
        attributes.add(attr("stray_conversion", BOOLEAN_VALUES, List.of(EntityType.SKELETON)));

        // ========== MANNEQUIN/PLAYER POSE ATTRIBUTES (1.21.9+) ==========
        // Mannequin is a player-like entity that doesn't need tab list entries
        // When Mannequin is available, PLAYER NPCs internally use Mannequin for performance
        // So we register pose for PLAYER type as well (only applies when using Mannequin internally)
        try {
            EntityType.valueOf("MANNEQUIN"); // Check if Mannequin is available
            // Register pose for PLAYER type (used internally as Mannequin on 1.21.9+)
            attributes.add(attr("pose", List.of("standing", "crouching", "swimming", "flying", "sleeping"), List.of(EntityType.PLAYER)));
        } catch (IllegalArgumentException ignored) {
            // Mannequin not available on this version - PLAYER NPCs use fake players
        }

        return attributes;
    }

    private static NpcAttribute attr(String name, List<String> values, List<EntityType> types) {
        // No-op apply function - NpcAttributeHandler handles application via FancySitula
        return new NpcAttribute(name, values, types, (npc, value) -> {});
    }
}
