package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancylib.translations.message.SimpleMessage;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.api.utils.NpcEquipmentSlot;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

public final class EquipmentCMD extends FancyContext {
    public static final EquipmentCMD INSTANCE = new EquipmentCMD();
    // Storing in a static variable to avoid re-creating the array each time suggestion is requested.
    private static final List<String> SLOT_SUGGESTIONS = Arrays.stream(NpcEquipmentSlot.values()).map(slot -> slot.name().toLowerCase()).toList();
    // Replace with Registry#stream after dropping 1.19.4 support.
    private static final List<String> MATERIAL_SUGGESTIONS = StreamSupport.stream(Registry.MATERIAL.spliterator(), false).filter(Material::isItem).map(material -> material.key().asString()).toList();

    private EquipmentCMD() {
    }

    @Command("npc equipment <npc> set <slot> <item>")
    @CommandPermission("fancynpcs.command.npc.equipment.set")
    public void onEquipmentSet(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final @NotNull NpcEquipmentSlot slot,
            final @NotNull ItemStack item
    ) {
        final CommandSender sender = actor.sender();
        // Calling the event and updating equipment if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.EQUIPMENT, new Object[]{slot, item}, sender).callEvent()) {
            npc.getData().addEquipment(slot, item);
            npc.updateForAll();
            translator.translate(item.getType() != Material.AIR ? "npc_equipment_set_item" : "npc_equipment_set_empty")
                    .withPrefix()
                    .replace("npc", npc.getData().getName())
                    .replace("slot", getTranslatedSlot(slot))
                    .addTagResolver(Placeholder.component("item", (item.getType() != Material.AIR) ? item.displayName().hoverEvent(item.asHoverEvent()) : Component.empty()))
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
        }
    }

    @Command("npc equipment <npc> clear")
    @CommandPermission("fancynpcs.command.npc.equipment.clear")
    public void onEquipmentClear(
            final BukkitCommandActor actor,
            final @NotNull Npc npc
    ) {
        final CommandSender sender = actor.sender();
        // Calling the event and clearing equipment if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.EQUIPMENT, null, sender).callEvent()) {
            // Entries must be set to null manually because clearing the map would prevent equipment from being updated. (Npc#update checks if map is empty)
            for (final NpcEquipmentSlot slot : NpcEquipmentSlot.values())
                npc.getData().getEquipment().put(slot, new ItemStack(Material.AIR));
            npc.updateForAll();
            translator.translate("npc_equipment_clear_success").withPrefix().replace("npc", npc.getData().getName()).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
        }
    }

    @Command("npc equipment <npc> list")
    @CommandPermission("fancynpcs.command.npc.equipment.list")
    public void onEquipmentList(
            final BukkitCommandActor actor,
            final @NotNull Npc npc
    ) {
        final CommandSender sender = actor.sender();
        // Sending error message if the list is empty or all items are Material.AIR.
        if (npc.getData().getEquipment().isEmpty() || npc.getData().getEquipment().values().stream().allMatch(item -> item == null || item.getType() == Material.AIR)) {
            translator.translate("npc_equipment_list_failure_empty").withPrefix().send(sender);
            return;
        }
        translator.translate("npc_equipment_list_header").send(sender);
        // Iterating over all equipment slots of this NPC and sending them to the sender.
        npc.getData().getEquipment().forEach((slot, item) -> {
            // Skipping null entries and Material.AIR, no need to display that.
            if (item == null || item.getType() == Material.AIR)
                return;
            translator.translate("npc_equipment_list_entry")
                    .replace("slot", getTranslatedSlot(slot))
                    .addTagResolver(Placeholder.component("item", item.displayName().hoverEvent(item.asHoverEvent())))
                    .send(sender);
        });
        translator.translate("npc_equipment_list_footer").send(sender);
    }

    @Command("npc equipment <npc> mirror [player]")
    @CommandPermission("fancynpcs.command.npc.equipment.mirror")
    public void onEquipmentMirror(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final @Optional @Nullable Player target
    ) {
        final CommandSender sender = actor.sender();
        final Player source;
        if (target != null) {
            source = target;
        } else if (actor.isPlayer()) {
            source = actor.requirePlayer();
        } else {
            translator.translate("command_player_only").withPrefix().send(sender);
            return;
        }

        final PlayerInventory inventory = source.getInventory();
        final Map<NpcEquipmentSlot, ItemStack> mirrored = new EnumMap<>(NpcEquipmentSlot.class);
        mirrored.put(NpcEquipmentSlot.HEAD, cloneOrAir(inventory.getHelmet()));
        mirrored.put(NpcEquipmentSlot.CHEST, cloneOrAir(inventory.getChestplate()));
        mirrored.put(NpcEquipmentSlot.LEGS, cloneOrAir(inventory.getLeggings()));
        mirrored.put(NpcEquipmentSlot.FEET, cloneOrAir(inventory.getBoots()));
        mirrored.put(NpcEquipmentSlot.MAINHAND, inventory.getItemInMainHand().clone());
        mirrored.put(NpcEquipmentSlot.OFFHAND, inventory.getItemInOffHand().clone());

        // Calling the event and mirroring equipment if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.EQUIPMENT, mirrored, sender).callEvent()) {
            mirrored.forEach(npc.getData()::addEquipment);
            npc.updateForAll();
            translator.translate("npc_equipment_mirror_success")
                    .withPrefix()
                    .replace("npc", npc.getData().getName())
                    .replace("player", source.getName())
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
        }
    }

    /* UTILITY METHODS */

    private @NotNull ItemStack cloneOrAir(final @Nullable ItemStack item) {
        return item == null ? new ItemStack(Material.AIR) : item.clone();
    }

    // NOTE: Might need to be improved later down the line, should get work done for now.
    private @NotNull String getTranslatedSlot(final @NotNull NpcEquipmentSlot slot) {
        return ((SimpleMessage) translator.translate(
                switch (slot) {
                    case MAINHAND -> "main_hand";
                    case OFFHAND -> "off_hand";
                    case HEAD -> "head";
                    case CHEST -> "chest";
                    case LEGS -> "legs";
                    case FEET -> "feet";
                }
        )).getMessage();
    }

}
