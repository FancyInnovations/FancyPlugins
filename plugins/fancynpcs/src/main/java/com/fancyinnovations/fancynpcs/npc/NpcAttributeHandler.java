package com.fancyinnovations.fancynpcs.npc;

import com.fancyinnovations.fancynpcs.api.FancyNpcsPlugin;
import com.fancyinnovations.fancynpcs.api.NpcAttribute;
import com.fancyinnovations.fancynpcs.api.NpcData;
import de.oliver.fancysitula.api.entities.FS_Entity;
import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;
import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket.EntityData;
import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket.EntityDataAccessor;
import de.oliver.fancysitula.api.utils.FS_EquipmentSlot;
import de.oliver.fancysitula.api.utils.ServerVersion;
import de.oliver.fancysitula.api.utils.entityData.*;
import de.oliver.fancysitula.factories.FancySitula;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

// Note: All FS_*Data classes are imported via wildcard above (entityData.*)

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        // Handle Villager/Zombie Villager specially - combine type and profession
        if (type == EntityType.VILLAGER || type == EntityType.ZOMBIE_VILLAGER) {
            EntityData villagerData = buildVillagerData(type, attrs);
            if (villagerData != null) {
                entityDataList.add(villagerData);
            }
        }

        // Handle Horse specially - combine variant and marking
        if (type == EntityType.HORSE) {
            EntityData horseData = buildHorseVariantData(attrs);
            if (horseData != null) {
                entityDataList.add(horseData);
            }
        }

        // Handle AbstractHorse saddle specially - saddles are equipment, not entity data flags
        // This applies to Horse, Donkey, Mule, Camel (entities that use EquipmentSlot.SADDLE)
        if (type == EntityType.HORSE || type == EntityType.DONKEY ||
            type == EntityType.MULE || type == EntityType.CAMEL ||
            type == EntityType.SKELETON_HORSE || type == EntityType.ZOMBIE_HORSE) {
            handleHorseSaddleEquipment(entity, type, attrs, fsPlayer);
        }

        for (Map.Entry<NpcAttribute, String> entry : attrs.entrySet()) {
            NpcAttribute attr = entry.getKey();
            String value = entry.getValue();

            if (attr == null || value == null || value.isEmpty()) {
                continue;
            }

            // Skip villager type/profession - already handled above
            String attrName = attr.getName().toLowerCase();
            if ((type == EntityType.VILLAGER || type == EntityType.ZOMBIE_VILLAGER)
                    && (attrName.equals("type") || attrName.equals("profession"))) {
                continue;
            }

            // Skip horse variant/marking - already handled above
            if (type == EntityType.HORSE && (attrName.equals("variant") || attrName.equals("marking"))) {
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
     * Build combined VillagerData from type and profession attributes.
     */
    private static EntityData buildVillagerData(EntityType type, Map<NpcAttribute, String> attrs) {
        String villagerType = null;
        String villagerProfession = null;

        for (Map.Entry<NpcAttribute, String> entry : attrs.entrySet()) {
            if (entry.getKey() == null) continue;
            String attrName = entry.getKey().getName().toLowerCase();
            if (attrName.equals("type")) {
                villagerType = entry.getValue();
            } else if (attrName.equals("profession")) {
                villagerProfession = entry.getValue();
            }
        }

        if (villagerType == null && villagerProfession == null) {
            return null;
        }

        StringBuilder combined = new StringBuilder();
        if (villagerType != null) {
            combined.append("type:").append(getVillagerTypeKey(villagerType));
        }
        if (villagerProfession != null) {
            if (combined.length() > 0) combined.append(",");
            combined.append("profession:").append(getVillagerProfessionKey(villagerProfession));
        }

        if (type == EntityType.VILLAGER) {
            return new EntityData(FS_VillagerData.VILLAGER_DATA, combined.toString());
        } else {
            EntityDataAccessor accessor = new EntityDataAccessor(
                    "net.minecraft.world.entity.monster.ZombieVillager",
                    "DATA_VILLAGER_DATA"
            );
            return new EntityData(accessor, combined.toString());
        }
    }

    /**
     * Build combined Horse variant data from variant and marking attributes.
     */
    private static EntityData buildHorseVariantData(Map<NpcAttribute, String> attrs) {
        String horseVariant = null;
        String horseMarking = null;

        for (Map.Entry<NpcAttribute, String> entry : attrs.entrySet()) {
            if (entry.getKey() == null) continue;
            String attrName = entry.getKey().getName().toLowerCase();
            if (attrName.equals("variant")) {
                horseVariant = entry.getValue();
            } else if (attrName.equals("marking")) {
                horseMarking = entry.getValue();
            }
        }

        if (horseVariant == null && horseMarking == null) {
            return null;
        }

        // Use defaults if only one is set
        int variant = horseVariant != null ? getHorseVariant(horseVariant) : FS_HorseData.VARIANT_WHITE;
        int marking = horseMarking != null ? getHorseMarking(horseMarking) : FS_HorseData.MARKING_NONE;

        return new EntityData(FS_HorseData.VARIANT, FS_HorseData.createVariantData(variant, marking));
    }

    /**
     * Handle saddle equipment for AbstractHorse entities (Horse, Donkey, Mule, Camel, etc.)
     * In modern Minecraft versions, horse saddles are controlled via EquipmentSlot.SADDLE,
     * NOT entity data flags.
     */
    private static void handleHorseSaddleEquipment(FS_Entity entity, EntityType type, Map<NpcAttribute, String> attrs, FS_RealPlayer fsPlayer) {
        for (Map.Entry<NpcAttribute, String> entry : attrs.entrySet()) {
            if (entry.getKey() == null) continue;
            String attrName = entry.getKey().getName().toLowerCase();
            if (attrName.equals("saddle") || attrName.equals("has_saddle")) {
                boolean hasSaddle = Boolean.parseBoolean(entry.getValue());
                Map<FS_EquipmentSlot, ItemStack> equipment = new HashMap<>();
                if (hasSaddle) {
                    equipment.put(FS_EquipmentSlot.SADDLE, new ItemStack(Material.SADDLE));
                } else {
                    equipment.put(FS_EquipmentSlot.SADDLE, new ItemStack(Material.AIR));
                }
                FancySitula.PACKET_FACTORY.createSetEquipmentPacket(entity.getId(), equipment).send(fsPlayer);
                return;
            }
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

            // ========== Slime/Magma Cube/Phantom Size Attributes ==========
            case "size" -> {
                if (type == EntityType.SLIME || type == EntityType.MAGMA_CUBE) {
                    try {
                        int size = Integer.parseInt(value);
                        yield new EntityData(FS_SlimeData.SIZE, size);
                    } catch (NumberFormatException e) {
                        yield null;
                    }
                }
                if (type == EntityType.PHANTOM) {
                    try {
                        int size = Integer.parseInt(value);
                        yield new EntityData(FS_PhantomData.SIZE, size);
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
                // Panda pose
                if (type == EntityType.PANDA) {
                    byte flags = 0;
                    switch (value.toLowerCase()) {
                        case "sitting" -> flags = FS_PandaData.FLAG_SITTING;
                        case "rolling" -> flags = FS_PandaData.FLAG_ROLLING;
                        case "on_back" -> flags = FS_PandaData.FLAG_ON_BACK;
                        case "sneezing" -> flags = FS_PandaData.FLAG_SNEEZING;
                    }
                    yield new EntityData(FS_PandaData.FLAGS, flags);
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
                if (type == EntityType.BEE) {
                    boolean angry = Boolean.parseBoolean(value);
                    byte flags = angry ? FS_BeeData.FLAG_ANGRY : 0;
                    yield new EntityData(FS_BeeData.FLAGS, flags);
                }
                // Enderman aggressive/creepy state
                if (type == EntityType.ENDERMAN) {
                    boolean angry = Boolean.parseBoolean(value);
                    yield new EntityData(FS_EndermanData.CREEPY, angry);
                }
                yield null;
            }
            case "interested" -> {
                if (type == EntityType.WOLF) {
                    boolean interested = Boolean.parseBoolean(value);
                    yield new EntityData(FS_WolfData.INTERESTED, interested);
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
                // Mooshroom variant (red/brown)
                if (type == EntityType.MOOSHROOM) {
                    String variant = value.equalsIgnoreCase("brown") ? FS_MushroomCowData.TYPE_BROWN : FS_MushroomCowData.TYPE_RED;
                    yield new EntityData(FS_MushroomCowData.TYPE, variant);
                }
                // Cow/Chicken/Pig variants (registry-based - 1.21.5+)
                Set<ServerVersion> v1_21_5_plus = Set.of(
                        ServerVersion.v1_21_5, ServerVersion.v1_21_6, ServerVersion.v1_21_7,
                        ServerVersion.v1_21_8, ServerVersion.v1_21_9, ServerVersion.v1_21_10, ServerVersion.v1_21_11
                );
                if (v1_21_5_plus.contains(ServerVersion.getCurrentVersion())) {
                    if (type == EntityType.COW) {
                        String variant = getRegistryVariantKey(value);
                        EntityDataAccessor accessor = new EntityDataAccessor(
                                "net.minecraft.world.entity.animal.Cow",
                                "DATA_VARIANT_ID"
                        );
                        yield new EntityData(accessor, variant);
                    }
                    if (type == EntityType.CHICKEN) {
                        String variant = getRegistryVariantKey(value);
                        EntityDataAccessor accessor = new EntityDataAccessor(
                                "net.minecraft.world.entity.animal.Chicken",
                                "DATA_VARIANT_ID"
                        );
                        yield new EntityData(accessor, variant);
                    }
                    if (type == EntityType.PIG) {
                        String variant = getRegistryVariantKey(value);
                        EntityDataAccessor accessor = new EntityDataAccessor(
                                "net.minecraft.world.entity.animal.Pig",
                                "DATA_VARIANT_ID"
                        );
                        yield new EntityData(accessor, variant);
                    }
                }
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
                // Zombie Villager type (biome)
                if (type == EntityType.ZOMBIE_VILLAGER) {
                    String typeKey = getVillagerTypeKey(value);
                    EntityDataAccessor accessor = new EntityDataAccessor(
                            "net.minecraft.world.entity.monster.ZombieVillager",
                            "DATA_VILLAGER_DATA"
                    );
                    yield new EntityData(accessor, "type:" + typeKey);
                }
                yield null;
            }

            // ========== Villager/Zombie Villager Profession ==========
            case "profession" -> {
                if (type == EntityType.VILLAGER) {
                    String professionKey = getVillagerProfessionKey(value);
                    yield new EntityData(FS_VillagerData.VILLAGER_DATA, "profession:" + professionKey);
                }
                if (type == EntityType.ZOMBIE_VILLAGER) {
                    String professionKey = getVillagerProfessionKey(value);
                    EntityDataAccessor accessor = new EntityDataAccessor(
                            "net.minecraft.world.entity.monster.ZombieVillager",
                            "DATA_VILLAGER_DATA"
                    );
                    yield new EntityData(accessor, "profession:" + professionKey);
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
            // Panda pose is now handled in the "pose" case above

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
            // NOTE: Horse/Donkey/Mule/Camel saddles are handled via equipment packets
            // in handleHorseSaddleEquipment(), not here. Only Pig/Strider use entity data.
            case "saddle", "has_saddle" -> {
                if (type == EntityType.PIG) {
                    boolean hasSaddle = Boolean.parseBoolean(value);
                    yield new EntityData(FS_PigData.SADDLE, hasSaddle);
                }
                if (type == EntityType.STRIDER) {
                    boolean hasSaddle = Boolean.parseBoolean(value);
                    yield new EntityData(FS_StriderData.SADDLE, hasSaddle);
                }
                // AbstractHorse saddles are handled via equipment, not entity data
                yield null;
            }
            case "cold" -> {
                if (type == EntityType.STRIDER) {
                    boolean cold = Boolean.parseBoolean(value);
                    yield new EntityData(FS_StriderData.SUFFOCATING, cold);
                }
                yield null;
            }

            // ========== Goat Attributes ==========
            case "screaming", "is_screaming" -> {
                if (type == EntityType.GOAT) {
                    boolean screaming = Boolean.parseBoolean(value);
                    yield new EntityData(FS_GoatData.IS_SCREAMING, screaming);
                }
                // Enderman screaming (same as creepy)
                if (type == EntityType.ENDERMAN) {
                    boolean screaming = Boolean.parseBoolean(value);
                    yield new EntityData(FS_EndermanData.CREEPY, screaming);
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
            case "charging_crossbow" -> {
                if (type == EntityType.PIGLIN) {
                    boolean charging = Boolean.parseBoolean(value);
                    yield new EntityData(FS_PiglinData.IS_CHARGING_CROSSBOW, charging);
                }
                yield null;
            }

            // ========== Camel Dashing ==========
            case "dashing" -> {
                if (type == EntityType.CAMEL) {
                    boolean dashing = Boolean.parseBoolean(value);
                    yield new EntityData(FS_CamelData.DASH, dashing);
                }
                yield null;
            }

            // ========== Raider Celebrating ==========
            case "celebrating" -> {
                if (type == EntityType.PILLAGER || type == EntityType.VINDICATOR ||
                    type == EntityType.EVOKER || type == EntityType.RAVAGER ||
                    type == EntityType.WITCH || type == EntityType.ILLUSIONER) {
                    boolean celebrating = Boolean.parseBoolean(value);
                    yield new EntityData(FS_RaiderData.IS_CELEBRATING, celebrating);
                }
                yield null;
            }

            // ========== Spellcaster Spell ==========
            case "spell" -> {
                if (type == EntityType.EVOKER || type == EntityType.ILLUSIONER) {
                    byte spell = getSpellId(value);
                    yield new EntityData(FS_SpellcasterData.SPELL_CASTING, spell);
                }
                yield null;
            }

            // ========== Armadillo/Sniffer State Attributes ==========
            case "state" -> {
                if (type == EntityType.ARMADILLO) {
                    String state = getArmadilloState(value);
                    yield new EntityData(FS_ArmadilloData.STATE, state);
                }
                if (type == EntityType.SNIFFER) {
                    String state = getSnifferState(value);
                    EntityDataAccessor accessor = new EntityDataAccessor(
                            "net.minecraft.world.entity.animal.sniffer.Sniffer",
                            "DATA_STATE"
                    );
                    yield new EntityData(accessor, state);
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
                // Ghast charging fireball
                if (type == EntityType.GHAST) {
                    boolean charging = Boolean.parseBoolean(value);
                    yield new EntityData(FS_GhastData.IS_CHARGING, charging);
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

            // ========== Snow Golem Attributes ==========
            case "pumpkin", "has_pumpkin" -> {
                if (type == EntityType.SNOW_GOLEM) {
                    boolean hasPumpkin = Boolean.parseBoolean(value);
                    byte flags = hasPumpkin ? FS_SnowGolemData.FLAG_PUMPKIN : 0;
                    yield new EntityData(FS_SnowGolemData.PUMPKIN, flags);
                }
                yield null;
            }

            // ========== Bat Attributes ==========
            case "resting", "hanging" -> {
                if (type == EntityType.BAT) {
                    boolean resting = Boolean.parseBoolean(value);
                    byte flags = resting ? FS_BatData.FLAG_RESTING : 0;
                    yield new EntityData(FS_BatData.FLAGS, flags);
                }
                yield null;
            }

            // ========== Polar Bear Attributes ==========
            case "standing", "rearing" -> {
                if (type == EntityType.POLAR_BEAR) {
                    boolean standing = Boolean.parseBoolean(value);
                    yield new EntityData(FS_PolarBearData.STANDING, standing);
                }
                yield null;
            }

            // ========== Pufferfish Attributes ==========
            case "puff_state", "inflation" -> {
                if (type == EntityType.PUFFERFISH) {
                    int puffState = getPuffState(value);
                    yield new EntityData(FS_PufferfishData.PUFF_STATE, puffState);
                }
                yield null;
            }

            // ========== Salmon Attributes (1.21.4+) ==========
            case "salmon_size" -> {
                if (type == EntityType.SALMON) {
                    int size = getSalmonSize(value);
                    yield new EntityData(FS_SalmonData.SIZE, size);
                }
                yield null;
            }

            // ========== Enderman Attributes ==========
            // Note: Enderman "angry" is handled in the Wolf/Bee "angry" case
            // Note: Enderman "screaming" is handled in the Goat "screaming" case
            case "creepy" -> {
                if (type == EntityType.ENDERMAN) {
                    boolean creepy = Boolean.parseBoolean(value);
                    yield new EntityData(FS_EndermanData.CREEPY, creepy);
                }
                yield null;
            }

            // ========== Ghast Attributes ==========
            // Note: Ghast "charging" is handled in the Vex "charging" case
            case "shooting" -> {
                if (type == EntityType.GHAST) {
                    boolean shooting = Boolean.parseBoolean(value);
                    yield new EntityData(FS_GhastData.IS_CHARGING, shooting);
                }
                yield null;
            }

            // ========== Guardian Attributes ==========
            case "moving", "spikes_retracted" -> {
                if (type == EntityType.GUARDIAN || type == EntityType.ELDER_GUARDIAN) {
                    boolean moving = Boolean.parseBoolean(value);
                    yield new EntityData(FS_GuardianData.MOVING, moving);
                }
                yield null;
            }

            // ========== Warden Attributes ==========
            case "anger", "anger_level" -> {
                if (type == EntityType.WARDEN) {
                    int angerLevel = getWardenAngerLevel(value);
                    yield new EntityData(FS_WardenData.ANGER_LEVEL, angerLevel);
                }
                yield null;
            }

            // ========== Turtle Attributes ==========
            case "laying_egg" -> {
                if (type == EntityType.TURTLE) {
                    boolean layingEgg = Boolean.parseBoolean(value);
                    yield new EntityData(FS_TurtleData.LAYING_EGG, layingEgg);
                }
                yield null;
            }

            // ========== Witch Attributes ==========
            case "drinking", "drinking_potion" -> {
                if (type == EntityType.WITCH) {
                    boolean drinking = Boolean.parseBoolean(value);
                    yield new EntityData(FS_WitchData.USING_ITEM, drinking);
                }
                yield null;
            }

            // ========== Hoglin Attributes ==========
            // Note: Hoglin "immune_to_zombification" is handled in the Piglin case

            // ========== Zoglin Attributes ==========
            // Note: Zoglin baby uses FS_ZoglinData, handled in baby attribute below

            // ========== Zombie Attributes ==========
            case "converting", "drowned_conversion" -> {
                if (type == EntityType.ZOMBIE || type == EntityType.HUSK) {
                    boolean converting = Boolean.parseBoolean(value);
                    yield new EntityData(FS_ZombieData.DROWNED_CONVERSION, converting);
                }
                yield null;
            }

            // ========== Skeleton Attributes ==========
            case "stray_conversion", "freezing" -> {
                if (type == EntityType.SKELETON) {
                    boolean converting = Boolean.parseBoolean(value);
                    yield new EntityData(FS_SkeletonData.STRAY_CONVERSION, converting);
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

    /**
     * Get Sniffer state key
     * Sniffer uses Sniffer.State enum
     */
    private static String getSnifferState(String stateName) {
        return switch (stateName.toUpperCase()) {
            case "IDLING" -> "IDLING";
            case "FEELING_HAPPY" -> "FEELING_HAPPY";
            case "SCENTING" -> "SCENTING";
            case "SNIFFING" -> "SNIFFING";
            case "SEARCHING" -> "SEARCHING";
            case "DIGGING" -> "DIGGING";
            case "RISING" -> "RISING";
            default -> "IDLING";
        };
    }

    private static String getRegistryVariantKey(String variantName) {
        String key = variantName.toLowerCase();
        return key.contains(":") ? key : "minecraft:" + key;
    }

    private static byte getSpellId(String spellName) {
        return switch (spellName.toUpperCase()) {
            case "NONE" -> FS_SpellcasterData.SPELL_NONE;
            case "SUMMON_VEX", "VEX" -> FS_SpellcasterData.SPELL_SUMMON_VEX;
            case "FANGS", "ATTACK" -> FS_SpellcasterData.SPELL_FANGS;
            case "WOLOLO", "COLOR" -> FS_SpellcasterData.SPELL_WOLOLO;
            case "DISAPPEAR", "INVISIBLE" -> FS_SpellcasterData.SPELL_DISAPPEAR;
            case "BLINDNESS", "BLIND" -> FS_SpellcasterData.SPELL_BLINDNESS;
            default -> FS_SpellcasterData.SPELL_NONE;
        };
    }

    private static int getPuffState(String value) {
        return switch (value.toUpperCase()) {
            case "SMALL", "DEFLATED", "0" -> FS_PufferfishData.STATE_SMALL;
            case "MEDIUM", "MID", "1" -> FS_PufferfishData.STATE_MEDIUM;
            case "FULL", "LARGE", "PUFFED", "2" -> FS_PufferfishData.STATE_FULL;
            default -> {
                try {
                    yield Math.min(2, Math.max(0, Integer.parseInt(value)));
                } catch (NumberFormatException e) {
                    yield FS_PufferfishData.STATE_SMALL;
                }
            }
        };
    }

    private static int getSalmonSize(String value) {
        return switch (value.toUpperCase()) {
            case "SMALL", "0" -> FS_SalmonData.SIZE_SMALL;
            case "MEDIUM", "1" -> FS_SalmonData.SIZE_MEDIUM;
            case "LARGE", "2" -> FS_SalmonData.SIZE_LARGE;
            default -> FS_SalmonData.SIZE_MEDIUM;
        };
    }

    private static int getWardenAngerLevel(String value) {
        return switch (value.toUpperCase()) {
            case "CALM", "NONE", "0" -> FS_WardenData.ANGER_CALM;
            case "AGITATED", "LOW" -> FS_WardenData.ANGER_AGITATED;
            case "ANGRY", "HIGH" -> FS_WardenData.ANGER_ANGRY;
            case "MAX", "MAXIMUM" -> FS_WardenData.ANGER_MAX;
            default -> {
                try {
                    yield Math.min(150, Math.max(0, Integer.parseInt(value)));
                } catch (NumberFormatException e) {
                    yield FS_WardenData.ANGER_CALM;
                }
            }
        };
    }
}
