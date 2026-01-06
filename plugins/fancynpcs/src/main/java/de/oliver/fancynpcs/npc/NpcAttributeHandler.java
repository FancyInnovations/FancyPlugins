package de.oliver.fancynpcs.npc;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancysitula.api.entities.FS_Entity;
import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;
import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket.EntityData;
import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket.EntityDataAccessor;
import de.oliver.fancysitula.api.utils.entityData.*;
import de.oliver.fancysitula.api.utils.entityData.FS_ArmadilloData;
import de.oliver.fancysitula.factories.FancySitula;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handles NPC attribute conversion and packet sending for FancySitula-based NPCs.
 * This replaces the old NMS-based attribute system.
 */
public class NpcAttributeHandler {

    /**
     * Apply all attributes from NpcData to the entity via FancySitula packets.
     *
     * @param entity   The FancySitula entity
     * @param data     The NPC data containing attributes
     * @param fsPlayer The player to send packets to
     */
    public static void applyAllAttributes(FS_Entity entity, NpcData data, FS_RealPlayer fsPlayer) {
        if (entity == null || data == null || fsPlayer == null) {
            return;
        }

        EntityType type = data.getType();
        Map<NpcAttribute, String> attrs = data.getAttributes();

        if (attrs == null || attrs.isEmpty()) {
            return;
        }

        List<EntityData> entityDataList = new ArrayList<>();

        for (Map.Entry<NpcAttribute, String> entry : attrs.entrySet()) {
            NpcAttribute attr = entry.getKey();
            String value = entry.getValue();

            if (attr == null || value == null || value.isEmpty()) {
                continue;
            }

            EntityData entityData = convertAttribute(type, attr.getName(), value);
            if (entityData != null) {
                entityDataList.add(entityData);
            }
        }

        // Send packet with all collected entity data
        if (!entityDataList.isEmpty()) {
            FancySitula.PACKET_FACTORY.createSetEntityDataPacket(entity.getId(), entityDataList).send(fsPlayer);
        }
    }

    /**
     * Convert a single attribute to EntityData for packet sending.
     *
     * @param type      Entity type
     * @param attrName  Attribute name
     * @param value     Attribute value as string
     * @return EntityData or null if not convertible
     */
    private static EntityData convertAttribute(EntityType type, String attrName, String value) {
        return switch (attrName.toLowerCase()) {
            // ========== Sheep Attributes ==========
            case "wool_color" -> {
                if (type == EntityType.SHEEP) {
                    int colorOrdinal = getDyeColorOrdinal(value);
                    yield new EntityData(FS_SheepData.WOOL, FS_SheepData.createWoolData(colorOrdinal, false));
                }
                yield null;
            }
            case "sheared" -> {
                if (type == EntityType.SHEEP) {
                    boolean sheared = Boolean.parseBoolean(value);
                    // Get current color from somewhere (default to white)
                    yield new EntityData(FS_SheepData.WOOL, FS_SheepData.createWoolData(0, sheared));
                }
                yield null;
            }

            // ========== Slime/Magma Cube Attributes ==========
            case "size" -> {
                if (type == EntityType.SLIME || type == EntityType.MAGMA_CUBE) {
                    try {
                        int size = Integer.parseInt(value);
                        yield new EntityData(FS_SlimeData.SIZE, size);
                    } catch (NumberFormatException e) {
                        yield null;
                    }
                }
                yield null;
            }

            // ========== Armor Stand Attributes ==========
            case "show_arms" -> {
                if (type == EntityType.ARMOR_STAND) {
                    boolean showArms = Boolean.parseBoolean(value);
                    byte flags = showArms ? FS_ArmorStandData.FLAG_ARMS : 0;
                    yield new EntityData(FS_ArmorStandData.CLIENT_FLAGS, flags);
                }
                yield null;
            }
            case "small" -> {
                if (type == EntityType.ARMOR_STAND) {
                    boolean small = Boolean.parseBoolean(value);
                    byte flags = small ? FS_ArmorStandData.FLAG_SMALL : 0;
                    yield new EntityData(FS_ArmorStandData.CLIENT_FLAGS, flags);
                }
                yield null;
            }
            case "no_baseplate" -> {
                if (type == EntityType.ARMOR_STAND) {
                    boolean noBaseplate = Boolean.parseBoolean(value);
                    byte flags = noBaseplate ? FS_ArmorStandData.FLAG_NO_BASEPLATE : 0;
                    yield new EntityData(FS_ArmorStandData.CLIENT_FLAGS, flags);
                }
                yield null;
            }
            case "marker" -> {
                if (type == EntityType.ARMOR_STAND) {
                    boolean marker = Boolean.parseBoolean(value);
                    byte flags = marker ? FS_ArmorStandData.FLAG_MARKER : 0;
                    yield new EntityData(FS_ArmorStandData.CLIENT_FLAGS, flags);
                }
                yield null;
            }

            // ========== TamableAnimal (Cat/Wolf/Parrot) Sitting ==========
            case "pose" -> {
                // Sitting pose for tamable animals
                if (type == EntityType.CAT || type == EntityType.WOLF || type == EntityType.PARROT) {
                    boolean sitting = value.equalsIgnoreCase("sitting");
                    byte flags = sitting ? FS_TamableAnimalData.FLAG_SITTING : 0;
                    // Also mark as tamed for sitting to work visually
                    if (sitting) {
                        flags |= FS_TamableAnimalData.FLAG_TAMED;
                    }
                    yield new EntityData(FS_TamableAnimalData.FLAGS, flags);
                }
                // Fox pose
                if (type == EntityType.FOX) {
                    byte flags = 0;
                    switch (value.toLowerCase()) {
                        case "sitting" -> flags = FS_FoxData.FLAG_SITTING;
                        case "sleeping" -> flags = FS_FoxData.FLAG_SLEEPING;
                        case "crouching" -> flags = FS_FoxData.FLAG_CROUCHING;
                    }
                    yield new EntityData(FS_FoxData.FLAGS, flags);
                }
                // Camel pose (sitting uses pose change tick)
                if (type == EntityType.CAMEL) {
                    // Camel sitting is controlled via pose change tick - negative value = sitting
                    long poseTick = value.equalsIgnoreCase("sitting") ? -1L : 0L;
                    yield new EntityData(FS_CamelData.LAST_POSE_CHANGE_TICK, poseTick);
                }
                yield null;
            }

            // ========== Wolf Attributes ==========
            case "angry" -> {
                if (type == EntityType.WOLF) {
                    boolean angry = Boolean.parseBoolean(value);
                    int angerTime = angry ? 400 : 0; // 400 ticks = 20 seconds
                    yield new EntityData(FS_WolfData.REMAINING_ANGER_TIME, angerTime);
                }
                yield null;
            }
            case "collar_color" -> {
                if (type == EntityType.WOLF) {
                    int colorOrdinal = getDyeColorOrdinal(value);
                    yield new EntityData(FS_WolfData.COLLAR_COLOR, colorOrdinal);
                }
                if (type == EntityType.CAT) {
                    int colorOrdinal = getDyeColorOrdinal(value);
                    yield new EntityData(FS_CatData.COLLAR_COLOR, colorOrdinal);
                }
                yield null;
            }

            // ========== Cat Attributes ==========
            case "lying" -> {
                if (type == EntityType.CAT) {
                    boolean lying = Boolean.parseBoolean(value);
                    yield new EntityData(FS_CatData.IS_LYING, lying);
                }
                yield null;
            }
            case "relaxed" -> {
                if (type == EntityType.CAT) {
                    boolean relaxed = Boolean.parseBoolean(value);
                    yield new EntityData(FS_CatData.RELAX_STATE_ONE, relaxed);
                }
                yield null;
            }

            // ========== Variant Attributes (various mobs) ==========
            case "variant" -> {
                // Axolotl variant (integer-based)
                if (type == EntityType.AXOLOTL) {
                    int variant = getAxolotlVariant(value);
                    yield new EntityData(FS_AxolotlData.VARIANT, variant);
                }
                // Parrot variant (integer-based)
                if (type == EntityType.PARROT) {
                    int variant = getParrotVariant(value);
                    yield new EntityData(FS_ParrotData.VARIANT, variant);
                }
                // Rabbit variant (integer-based)
                if (type == EntityType.RABBIT) {
                    int variant = getRabbitVariant(value);
                    yield new EntityData(FS_RabbitData.VARIANT, variant);
                }
                // Llama variant (integer-based)
                if (type == EntityType.LLAMA || type == EntityType.TRADER_LLAMA) {
                    int variant = getLlamaVariant(value);
                    yield new EntityData(FS_LlamaData.VARIANT, variant);
                }
                // Horse variant (combined with marking - uses default marking)
                if (type == EntityType.HORSE) {
                    int variant = getHorseVariant(value);
                    yield new EntityData(FS_HorseData.VARIANT, FS_HorseData.createVariantData(variant, FS_HorseData.MARKING_NONE));
                }
                // Wolf variant (Holder<WolfVariant>) - uses server registry lookup
                if (type == EntityType.WOLF) {
                    String variant = getWolfVariantKey(value);
                    yield new EntityData(FS_WolfData.VARIANT, variant);
                }
                // Cat variant (Holder<CatVariant>) - uses server registry lookup
                if (type == EntityType.CAT) {
                    String variant = getCatVariantKey(value);
                    yield new EntityData(FS_CatData.VARIANT, variant);
                }
                // Frog variant (Holder<FrogVariant>) - uses server registry lookup
                if (type == EntityType.FROG) {
                    String variant = getFrogVariantKey(value);
                    yield new EntityData(FS_FrogData.VARIANT, variant);
                }
                yield null;
            }
            // Fox type (red/snow) - different from variant in Attributes.java
            case "type" -> {
                if (type == EntityType.FOX) {
                    int variant = value.equalsIgnoreCase("snow") ? FS_FoxData.VARIANT_SNOW : FS_FoxData.VARIANT_RED;
                    yield new EntityData(FS_FoxData.VARIANT, variant);
                }
                // Villager type (biome)
                if (type == EntityType.VILLAGER) {
                    String typeKey = getVillagerTypeKey(value);
                    yield new EntityData(FS_VillagerData.VILLAGER_DATA, "type:" + typeKey);
                }
                yield null;
            }

            // ========== Villager Profession ==========
            case "profession" -> {
                if (type == EntityType.VILLAGER) {
                    String professionKey = getVillagerProfessionKey(value);
                    yield new EntityData(FS_VillagerData.VILLAGER_DATA, "profession:" + professionKey);
                }
                yield null;
            }
            case "playing_dead" -> {
                if (type == EntityType.AXOLOTL) {
                    boolean playingDead = Boolean.parseBoolean(value);
                    yield new EntityData(FS_AxolotlData.PLAYING_DEAD, playingDead);
                }
                yield null;
            }

            // ========== Panda Attributes ==========
            case "main_gene", "gene" -> {
                if (type == EntityType.PANDA) {
                    byte gene = getPandaGene(value);
                    yield new EntityData(FS_PandaData.MAIN_GENE, gene);
                }
                yield null;
            }
            case "hidden_gene" -> {
                if (type == EntityType.PANDA) {
                    byte gene = getPandaGene(value);
                    yield new EntityData(FS_PandaData.HIDDEN_GENE, gene);
                }
                yield null;
            }
            case "rolling" -> {
                if (type == EntityType.PANDA) {
                    boolean rolling = Boolean.parseBoolean(value);
                    byte flags = rolling ? FS_PandaData.FLAG_ROLLING : 0;
                    yield new EntityData(FS_PandaData.FLAGS, flags);
                }
                yield null;
            }
            case "on_back" -> {
                if (type == EntityType.PANDA) {
                    boolean onBack = Boolean.parseBoolean(value);
                    byte flags = onBack ? FS_PandaData.FLAG_ON_BACK : 0;
                    yield new EntityData(FS_PandaData.FLAGS, flags);
                }
                yield null;
            }

            // ========== Bee Attributes ==========
            case "has_nectar" -> {
                if (type == EntityType.BEE) {
                    boolean hasNectar = Boolean.parseBoolean(value);
                    byte flags = hasNectar ? FS_BeeData.FLAG_HAS_NECTAR : 0;
                    yield new EntityData(FS_BeeData.FLAGS, flags);
                }
                yield null;
            }
            case "has_stung" -> {
                if (type == EntityType.BEE) {
                    boolean hasStung = Boolean.parseBoolean(value);
                    byte flags = hasStung ? FS_BeeData.FLAG_HAS_STUNG : 0;
                    yield new EntityData(FS_BeeData.FLAGS, flags);
                }
                yield null;
            }

            // ========== Pig/Strider Saddle Attributes ==========
            case "saddle", "has_saddle" -> {
                if (type == EntityType.PIG) {
                    boolean hasSaddle = Boolean.parseBoolean(value);
                    yield new EntityData(FS_PigData.SADDLE, hasSaddle);
                }
                if (type == EntityType.STRIDER) {
                    boolean hasSaddle = Boolean.parseBoolean(value);
                    yield new EntityData(FS_StriderData.SADDLE, hasSaddle);
                }
                yield null;
            }

            // ========== Goat Attributes ==========
            case "screaming", "is_screaming" -> {
                if (type == EntityType.GOAT) {
                    boolean screaming = Boolean.parseBoolean(value);
                    yield new EntityData(FS_GoatData.IS_SCREAMING, screaming);
                }
                yield null;
            }
            case "has_left_horn" -> {
                if (type == EntityType.GOAT) {
                    boolean hasLeftHorn = Boolean.parseBoolean(value);
                    yield new EntityData(FS_GoatData.HAS_LEFT_HORN, hasLeftHorn);
                }
                yield null;
            }
            case "has_right_horn" -> {
                if (type == EntityType.GOAT) {
                    boolean hasRightHorn = Boolean.parseBoolean(value);
                    yield new EntityData(FS_GoatData.HAS_RIGHT_HORN, hasRightHorn);
                }
                yield null;
            }

            // ========== Creeper Attributes ==========
            case "powered", "charged" -> {
                if (type == EntityType.CREEPER) {
                    boolean powered = Boolean.parseBoolean(value);
                    yield new EntityData(FS_CreeperData.IS_POWERED, powered);
                }
                yield null;
            }
            case "ignited" -> {
                if (type == EntityType.CREEPER) {
                    boolean ignited = Boolean.parseBoolean(value);
                    yield new EntityData(FS_CreeperData.IS_IGNITED, ignited);
                }
                yield null;
            }

            // ========== Piglin/Allay Dancing Attributes ==========
            case "dancing", "is_dancing" -> {
                if (type == EntityType.PIGLIN) {
                    boolean dancing = Boolean.parseBoolean(value);
                    yield new EntityData(FS_PiglinData.IS_DANCING, dancing);
                }
                if (type == EntityType.ALLAY) {
                    boolean dancing = Boolean.parseBoolean(value);
                    yield new EntityData(FS_AllayData.DANCING, dancing);
                }
                yield null;
            }
            case "immune_to_zombification" -> {
                if (type == EntityType.PIGLIN || type == EntityType.PIGLIN_BRUTE) {
                    boolean immune = Boolean.parseBoolean(value);
                    yield new EntityData(FS_PiglinData.IMMUNE_TO_ZOMBIFICATION, immune);
                }
                yield null;
            }

            // ========== Armadillo Attributes ==========
            case "state" -> {
                if (type == EntityType.ARMADILLO) {
                    String state = getArmadilloState(value);
                    yield new EntityData(FS_ArmadilloData.STATE, state);
                }
                yield null;
            }

            // ========== Shulker Attributes ==========
            case "color" -> {
                if (type == EntityType.SHULKER) {
                    byte colorOrdinal = (byte) getDyeColorOrdinal(value);
                    yield new EntityData(FS_ShulkerData.COLOR, colorOrdinal);
                }
                yield null;
            }

            // ========== Horse Marking Attributes ==========
            case "marking" -> {
                if (type == EntityType.HORSE) {
                    int marking = getHorseMarking(value);
                    // Use default white variant since marking is separate
                    yield new EntityData(FS_HorseData.VARIANT, FS_HorseData.createVariantData(FS_HorseData.VARIANT_WHITE, marking));
                }
                yield null;
            }

            // ========== Vex Attributes ==========
            case "charging" -> {
                if (type == EntityType.VEX) {
                    boolean charging = Boolean.parseBoolean(value);
                    byte flags = charging ? FS_VexData.FLAG_CHARGING : 0;
                    yield new EntityData(FS_VexData.FLAGS, flags);
                }
                yield null;
            }

            // ========== Entity-wide Attributes ==========
            case "silent" -> {
                boolean silent = Boolean.parseBoolean(value);
                yield new EntityData(FS_EntityData.SILENT, silent);
            }

            // ========== Living Entity Attributes ==========
            case "use_item" -> {
                // Only works for living entities
                // Bit 0x01 = is hand active, Bit 0x02 = active hand (0 = main, 1 = off)
                byte flags = 0;
                switch (value.toLowerCase()) {
                    case "main_hand" -> flags = 0x01; // hand active, main hand
                    case "off_hand" -> flags = 0x03;  // hand active (0x01) + off hand (0x02)
                    case "none" -> flags = 0;
                }
                yield new EntityData(FS_LivingEntityData.LIVING_ENTITY_FLAGS, flags);
            }

            // ========== Tropical Fish Attributes ==========
            case "pattern" -> {
                if (type == EntityType.TROPICAL_FISH) {
                    int[] sizeAndPattern = FS_TropicalFishData.getPatternAndSize(value);
                    // Create variant with default colors (white body, white pattern)
                    int variant = FS_TropicalFishData.createVariantData(sizeAndPattern[0], sizeAndPattern[1], 0, 0);
                    yield new EntityData(FS_TropicalFishData.VARIANT, variant);
                }
                yield null;
            }
            case "body_color" -> {
                if (type == EntityType.TROPICAL_FISH) {
                    int bodyColor = getDyeColorOrdinal(value);
                    // Create variant with default pattern (kob) and pattern color (white)
                    int variant = FS_TropicalFishData.createVariantData(FS_TropicalFishData.SIZE_SMALL, FS_TropicalFishData.PATTERN_KOB, bodyColor, 0);
                    yield new EntityData(FS_TropicalFishData.VARIANT, variant);
                }
                yield null;
            }
            case "pattern_color" -> {
                if (type == EntityType.TROPICAL_FISH) {
                    int patternColor = getDyeColorOrdinal(value);
                    // Create variant with default pattern (kob) and body color (white)
                    int variant = FS_TropicalFishData.createVariantData(FS_TropicalFishData.SIZE_SMALL, FS_TropicalFishData.PATTERN_KOB, 0, patternColor);
                    yield new EntityData(FS_TropicalFishData.VARIANT, variant);
                }
                yield null;
            }

            // Default: attribute not handled
            default -> null;
        };
    }

    // ========== Helper methods for variant/color conversions ==========

    private static int getDyeColorOrdinal(String colorName) {
        if (colorName == null || colorName.isEmpty() || colorName.equalsIgnoreCase("none")) {
            return 0; // WHITE
        }
        return switch (colorName.toUpperCase()) {
            case "WHITE" -> 0;
            case "ORANGE" -> 1;
            case "MAGENTA" -> 2;
            case "LIGHT_BLUE" -> 3;
            case "YELLOW" -> 4;
            case "LIME" -> 5;
            case "PINK" -> 6;
            case "GRAY" -> 7;
            case "LIGHT_GRAY" -> 8;
            case "CYAN" -> 9;
            case "PURPLE" -> 10;
            case "BLUE" -> 11;
            case "BROWN" -> 12;
            case "GREEN" -> 13;
            case "RED" -> 14;
            case "BLACK" -> 15;
            default -> 0;
        };
    }

    private static int getAxolotlVariant(String variantName) {
        return switch (variantName.toUpperCase()) {
            case "LUCY", "PINK" -> FS_AxolotlData.VARIANT_LUCY;
            case "WILD", "BROWN" -> FS_AxolotlData.VARIANT_WILD;
            case "GOLD", "YELLOW" -> FS_AxolotlData.VARIANT_GOLD;
            case "CYAN" -> FS_AxolotlData.VARIANT_CYAN;
            case "BLUE" -> FS_AxolotlData.VARIANT_BLUE;
            default -> FS_AxolotlData.VARIANT_LUCY;
        };
    }

    private static int getParrotVariant(String variantName) {
        return switch (variantName.toUpperCase()) {
            case "RED_BLUE", "RED" -> FS_ParrotData.VARIANT_RED_BLUE;
            case "BLUE" -> FS_ParrotData.VARIANT_BLUE;
            case "GREEN" -> FS_ParrotData.VARIANT_GREEN;
            case "YELLOW_BLUE", "YELLOW" -> FS_ParrotData.VARIANT_YELLOW_BLUE;
            case "GRAY", "GREY" -> FS_ParrotData.VARIANT_GRAY;
            default -> FS_ParrotData.VARIANT_RED_BLUE;
        };
    }

    private static int getRabbitVariant(String variantName) {
        return switch (variantName.toUpperCase()) {
            case "BROWN" -> FS_RabbitData.VARIANT_BROWN;
            case "WHITE" -> FS_RabbitData.VARIANT_WHITE;
            case "BLACK" -> FS_RabbitData.VARIANT_BLACK;
            case "WHITE_SPLOTCHED", "SPLOTCHED" -> FS_RabbitData.VARIANT_WHITE_SPLOTCHED;
            case "GOLD", "GOLDEN" -> FS_RabbitData.VARIANT_GOLD;
            case "SALT", "SALT_AND_PEPPER" -> FS_RabbitData.VARIANT_SALT;
            case "EVIL", "KILLER", "KILLER_BUNNY" -> FS_RabbitData.VARIANT_EVIL;
            default -> FS_RabbitData.VARIANT_BROWN;
        };
    }

    private static byte getPandaGene(String geneName) {
        return switch (geneName.toUpperCase()) {
            case "NORMAL" -> FS_PandaData.GENE_NORMAL;
            case "LAZY" -> FS_PandaData.GENE_LAZY;
            case "WORRIED" -> FS_PandaData.GENE_WORRIED;
            case "PLAYFUL" -> FS_PandaData.GENE_PLAYFUL;
            case "BROWN" -> FS_PandaData.GENE_BROWN;
            case "WEAK" -> FS_PandaData.GENE_WEAK;
            case "AGGRESSIVE" -> FS_PandaData.GENE_AGGRESSIVE;
            default -> FS_PandaData.GENE_NORMAL;
        };
    }

    private static String getArmadilloState(String stateName) {
        return switch (stateName.toUpperCase()) {
            case "IDLE" -> FS_ArmadilloData.STATE_IDLE;
            case "ROLLING" -> FS_ArmadilloData.STATE_ROLLING;
            case "SCARED" -> FS_ArmadilloData.STATE_SCARED;
            case "UNROLLING" -> FS_ArmadilloData.STATE_UNROLLING;
            default -> FS_ArmadilloData.STATE_IDLE;
        };
    }

    private static int getLlamaVariant(String variantName) {
        return switch (variantName.toUpperCase()) {
            case "CREAMY" -> FS_LlamaData.VARIANT_CREAMY;
            case "WHITE" -> FS_LlamaData.VARIANT_WHITE;
            case "BROWN" -> FS_LlamaData.VARIANT_BROWN;
            case "GRAY", "GREY" -> FS_LlamaData.VARIANT_GRAY;
            default -> FS_LlamaData.VARIANT_CREAMY;
        };
    }

    private static int getHorseVariant(String variantName) {
        return switch (variantName.toUpperCase()) {
            case "WHITE" -> FS_HorseData.VARIANT_WHITE;
            case "CREAMY" -> FS_HorseData.VARIANT_CREAMY;
            case "CHESTNUT" -> FS_HorseData.VARIANT_CHESTNUT;
            case "BROWN" -> FS_HorseData.VARIANT_BROWN;
            case "BLACK" -> FS_HorseData.VARIANT_BLACK;
            case "GRAY", "GREY" -> FS_HorseData.VARIANT_GRAY;
            case "DARK_BROWN" -> FS_HorseData.VARIANT_DARK_BROWN;
            default -> FS_HorseData.VARIANT_WHITE;
        };
    }

    private static int getHorseMarking(String markingName) {
        return switch (markingName.toUpperCase()) {
            case "NONE" -> FS_HorseData.MARKING_NONE;
            case "WHITE" -> FS_HorseData.MARKING_WHITE;
            case "WHITE_FIELD" -> FS_HorseData.MARKING_WHITE_FIELD;
            case "WHITE_DOTS" -> FS_HorseData.MARKING_WHITE_DOTS;
            case "BLACK_DOTS" -> FS_HorseData.MARKING_BLACK_DOTS;
            default -> FS_HorseData.MARKING_NONE;
        };
    }

    /**
     * Get Wolf variant key for Holder<WolfVariant> lookup
     * Returns minecraft:variant_name format
     */
    private static String getWolfVariantKey(String variantName) {
        return switch (variantName.toUpperCase()) {
            case "PALE" -> "minecraft:pale";
            case "SPOTTED" -> "minecraft:spotted";
            case "SNOWY" -> "minecraft:snowy";
            case "BLACK" -> "minecraft:black";
            case "ASHEN" -> "minecraft:ashen";
            case "RUSTY" -> "minecraft:rusty";
            case "WOODS" -> "minecraft:woods";
            case "CHESTNUT" -> "minecraft:chestnut";
            case "STRIPED" -> "minecraft:striped";
            default -> "minecraft:pale";
        };
    }

    /**
     * Get Cat variant key for Holder<CatVariant> lookup
     * Returns minecraft:variant_name format
     */
    private static String getCatVariantKey(String variantName) {
        return switch (variantName.toUpperCase()) {
            case "TABBY" -> "minecraft:tabby";
            case "BLACK" -> "minecraft:black";
            case "RED" -> "minecraft:red";
            case "SIAMESE" -> "minecraft:siamese";
            case "BRITISH_SHORTHAIR" -> "minecraft:british_shorthair";
            case "CALICO" -> "minecraft:calico";
            case "PERSIAN" -> "minecraft:persian";
            case "RAGDOLL" -> "minecraft:ragdoll";
            case "WHITE" -> "minecraft:white";
            case "JELLIE" -> "minecraft:jellie";
            case "ALL_BLACK" -> "minecraft:all_black";
            default -> "minecraft:tabby";
        };
    }

    /**
     * Get Frog variant key for Holder<FrogVariant> lookup
     * Returns minecraft:variant_name format
     */
    private static String getFrogVariantKey(String variantName) {
        return switch (variantName.toUpperCase()) {
            case "TEMPERATE" -> "minecraft:temperate";
            case "WARM" -> "minecraft:warm";
            case "COLD" -> "minecraft:cold";
            default -> "minecraft:temperate";
        };
    }

    /**
     * Get Villager profession key for VillagerData
     * Returns minecraft:profession_name format
     */
    private static String getVillagerProfessionKey(String professionName) {
        return switch (professionName.toUpperCase()) {
            case "NONE" -> "minecraft:none";
            case "ARMORER" -> "minecraft:armorer";
            case "BUTCHER" -> "minecraft:butcher";
            case "CARTOGRAPHER" -> "minecraft:cartographer";
            case "CLERIC" -> "minecraft:cleric";
            case "FARMER" -> "minecraft:farmer";
            case "FISHERMAN" -> "minecraft:fisherman";
            case "FLETCHER" -> "minecraft:fletcher";
            case "LEATHERWORKER" -> "minecraft:leatherworker";
            case "LIBRARIAN" -> "minecraft:librarian";
            case "MASON" -> "minecraft:mason";
            case "NITWIT" -> "minecraft:nitwit";
            case "SHEPHERD" -> "minecraft:shepherd";
            case "TOOLSMITH" -> "minecraft:toolsmith";
            case "WEAPONSMITH" -> "minecraft:weaponsmith";
            default -> "minecraft:none";
        };
    }

    /**
     * Get Villager type (biome) key for VillagerData
     * Returns minecraft:biome_name format
     */
    private static String getVillagerTypeKey(String typeName) {
        return switch (typeName.toUpperCase()) {
            case "DESERT" -> "minecraft:desert";
            case "JUNGLE" -> "minecraft:jungle";
            case "PLAINS" -> "minecraft:plains";
            case "SAVANNA" -> "minecraft:savanna";
            case "SNOW" -> "minecraft:snow";
            case "SWAMP" -> "minecraft:swamp";
            case "TAIGA" -> "minecraft:taiga";
            default -> "minecraft:plains";
        };
    }
}
