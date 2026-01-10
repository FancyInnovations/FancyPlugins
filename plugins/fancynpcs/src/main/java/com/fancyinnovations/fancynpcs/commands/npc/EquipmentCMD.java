package com.fancyinnovations.fancynpcs.commands.npc;

import com.fancyinnovations.fancynpcs.FancyNpcs;
import com.fancyinnovations.fancynpcs.api.Npc;
import com.fancyinnovations.fancynpcs.api.events.NpcModifyEvent;
import com.fancyinnovations.fancynpcs.api.utils.NpcEquipmentSlot;
import com.fancyinnovations.fancynpcs.commands.exceptions.ReplyingParseException;
import de.oliver.fancylib.translations.Translator;
import de.oliver.fancylib.translations.message.SimpleMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

public enum EquipmentCMD {
    INSTANCE; // SINGLETON

    private static final List<String> MATERIAL_SUGGESTIONS = StreamSupport.stream(Registry.MATERIAL.spliterator(), false).filter(Material::isItem).map(material -> material.key().asString()).toList();

    private static final List<String> HEAD_ITEMS = MATERIAL_SUGGESTIONS.stream()
            .filter(m -> m.contains("helmet") || m.contains("_head") || m.contains("_skull")
                    || m.equals("minecraft:carved_pumpkin") || m.equals("minecraft:jack_o_lantern")
                    || m.contains("_banner"))
            .toList();
    private static final List<String> CHEST_ITEMS = MATERIAL_SUGGESTIONS.stream()
            .filter(m -> m.contains("chestplate") || m.equals("minecraft:elytra"))
            .toList();
    private static final List<String> LEGS_ITEMS = MATERIAL_SUGGESTIONS.stream()
            .filter(m -> m.contains("leggings"))
            .toList();
    private static final List<String> FEET_ITEMS = MATERIAL_SUGGESTIONS.stream()
            .filter(m -> m.contains("boots"))
            .toList();
    private static final List<String> HORSE_BODY_ITEMS = MATERIAL_SUGGESTIONS.stream()
            .filter(m -> m.contains("horse_armor"))
            .toList();
    private static final List<String> WOLF_BODY_ITEMS = MATERIAL_SUGGESTIONS.stream()
            .filter(m -> m.contains("wolf_armor"))
            .toList();
    private static final List<String> LLAMA_BODY_ITEMS = MATERIAL_SUGGESTIONS.stream()
            .filter(m -> m.contains("_carpet"))
            .toList();

    private static final Set<EntityType> HORSE_TYPES = Set.of(EntityType.HORSE, EntityType.ZOMBIE_HORSE, EntityType.SKELETON_HORSE);
    private static final Set<EntityType> LLAMA_TYPES = Set.of(EntityType.LLAMA, EntityType.TRADER_LLAMA);

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc equipment <npc> set <slot> <item>")
    @Permission("fancynpcs.command.npc.equipment.set")
    public void onEquipmentSet(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull NpcEquipmentSlot slot,
            final @NotNull @Argument(parserName = "EquipmentCMD/item") ItemStack item
    ) {
        if (!NpcEquipmentSlot.isValidSlot(npc.getData().getType(), slot)) {
            translator.translate("npc_equipment_invalid_slot_for_entity")
                    .replace("slot", getTranslatedSlot(slot))
                    .replace("type", npc.getData().getType().name().toLowerCase().replace("_", " "))
                    .send(sender);
            return;
        }

        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.EQUIPMENT, new Object[]{slot, item}, sender).callEvent()) {
            npc.getData().addEquipment(slot, item);
            npc.updateForAll();
            translator.translate(item.getType() != Material.AIR ? "npc_equipment_set_item" : "npc_equipment_set_empty")
                    .replace("npc", npc.getData().getName())
                    .replace("slot", getTranslatedSlot(slot))
                    .addTagResolver(Placeholder.component("item", (item.getType() != Material.AIR) ? item.displayName().hoverEvent(item.asHoverEvent()) : Component.empty()))
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    @Command("npc equipment <npc> clear")
    @Permission("fancynpcs.command.npc.equipment.clear")
    public void onEquipmentClear(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc
    ) {
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.EQUIPMENT, null, sender).callEvent()) {
            for (final NpcEquipmentSlot slot : NpcEquipmentSlot.values())
                npc.getData().getEquipment().put(slot, new ItemStack(Material.AIR));
            npc.updateForAll();
            translator.translate("npc_equipment_clear_success").replace("npc", npc.getData().getName()).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    @Command("npc equipment <npc> list")
    @Permission("fancynpcs.command.npc.equipment.list")
    public void onEquipmentList(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc
    ) {
        if (npc.getData().getEquipment().isEmpty() || npc.getData().getEquipment().values().stream().allMatch(item -> item == null || item.getType() == Material.AIR)) {
            translator.translate("npc_equipment_list_failure_empty").send(sender);
            return;
        }
        translator.translate("npc_equipment_list_header").send(sender);
        npc.getData().getEquipment().forEach((slot, item) -> {
            if (item == null || item.getType() == Material.AIR)
                return;
            translator.translate("npc_equipment_list_entry")
                    .replace("slot", getTranslatedSlot(slot))
                    .addTagResolver(Placeholder.component("item", item.displayName().hoverEvent(item.asHoverEvent())))
                    .send(sender);
        });
        translator.translate("npc_equipment_list_footer").send(sender);
    }

    /* PARSERS AND SUGGESTIONS */

    @Parser(name = "", suggestions = "EquipmentCMD/slot")
    public NpcEquipmentSlot parseSlot(final CommandContext<CommandSender> context, final CommandInput input) {
        final String value = input.readString().toLowerCase();
        final @Nullable NpcEquipmentSlot slot = NpcEquipmentSlot.parse(value);
        if (slot == null)
            throw ReplyingParseException.replying(() -> translator.translate("command_invalid_equipment_slot").replaceStripped("input", value).send(context.sender()));
        return slot;
    }

    @Parser(name = "EquipmentCMD/item", suggestions = "EquipmentCMD/item")
    public ItemStack parseItem(final CommandContext<CommandSender> context, final CommandInput input) {
        final String value = input.readString().toLowerCase();
        if (value.equals("@none"))
            return new ItemStack(Material.AIR);
        else if (value.equals("@hand") && context.sender() instanceof Player player)
            return player.getInventory().getItemInMainHand().clone();
        else {
            final @Nullable NamespacedKey key = NamespacedKey.fromString(value);
            if (key == null)
                throw ReplyingParseException.replying(() -> translator.translate("command_invalid_material").replaceStripped("input", value).send(context.sender()));
            final @Nullable Material material = Registry.MATERIAL.get(key);
            if (material == null || !material.isItem())
                throw ReplyingParseException.replying(() -> translator.translate("command_invalid_material").replaceStripped("input", value).send(context.sender()));
            return new ItemStack(material);
        }
    }

    @Suggestions("EquipmentCMD/item")
    public List<String> suggestItem(final CommandContext<CommandSender> context, final CommandInput input) {
        List<String> suggestions = getSlotSpecificItems(context);
        List<String> result = new ArrayList<>(suggestions);
        result.add("@none");
        if (context.sender() instanceof Player)
            result.add("@hand");
        return result;
    }

    @Suggestions("EquipmentCMD/slot")
    public List<String> suggestSlot(final CommandContext<CommandSender> context, final CommandInput input) {
        try {
            Npc npc = context.get("npc");
            if (npc != null) {
                return NpcEquipmentSlot.getValidSlots(npc.getData().getType())
                        .stream()
                        .map(slot -> slot.name().toLowerCase())
                        .toList();
            }
        } catch (Exception ignored) {}
        return Arrays.stream(NpcEquipmentSlot.values())
                .map(slot -> slot.name().toLowerCase())
                .toList();
    }

    /* UTILITY METHODS */

    private List<String> getSlotSpecificItems(final CommandContext<CommandSender> context) {
        try {
            Npc npc = context.get("npc");
            NpcEquipmentSlot slot = context.get("slot");
            if (npc == null || slot == null) {
                return MATERIAL_SUGGESTIONS;
            }
            EntityType entityType = npc.getData().getType();
            return switch (slot) {
                case HEAD -> HEAD_ITEMS;
                case CHEST -> CHEST_ITEMS;
                case LEGS -> LEGS_ITEMS;
                case FEET -> FEET_ITEMS;
                case BODY -> getBodyItemsForEntity(entityType);
                case MAINHAND, OFFHAND, SADDLE -> MATERIAL_SUGGESTIONS;
            };
        } catch (Exception ignored) {
            return MATERIAL_SUGGESTIONS;
        }
    }

    private List<String> getBodyItemsForEntity(EntityType entityType) {
        if (HORSE_TYPES.contains(entityType)) {
            return HORSE_BODY_ITEMS;
        } else if (LLAMA_TYPES.contains(entityType)) {
            return LLAMA_BODY_ITEMS;
        } else if (entityType == EntityType.WOLF) {
            return WOLF_BODY_ITEMS;
        }
        return MATERIAL_SUGGESTIONS;
    }

    private @NotNull String getTranslatedSlot(final @NotNull NpcEquipmentSlot slot) {
        return ((SimpleMessage) translator.translate(
                switch (slot) {
                    case MAINHAND -> "main_hand";
                    case OFFHAND -> "off_hand";
                    case HEAD -> "head";
                    case CHEST -> "chest";
                    case LEGS -> "legs";
                    case FEET -> "feet";
                    case BODY -> "body";
                    case SADDLE -> "saddle";
                }
        )).getMessage();
    }

}
