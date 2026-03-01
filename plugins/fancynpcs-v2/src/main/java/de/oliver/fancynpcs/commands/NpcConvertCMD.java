package de.oliver.fancynpcs.commands;

import de.oliver.fancylib.ReflectionUtils;
import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.api.utils.NpcEquipmentSlot;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.MobType;
import net.citizensnpcs.api.trait.trait.Owner;
import net.citizensnpcs.trait.CommandTrait;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.ScoreboardTrait;
import net.citizensnpcs.trait.SkinTrait;
import net.citizensnpcs.trait.text.Text;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.Map;
import java.util.UUID;

public final class NpcConvertCMD {

    public static final NpcConvertCMD INSTANCE = new NpcConvertCMD();

    private final FancyNpcs plugin = FancyNpcs.getInstance();
    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    private NpcConvertCMD() {
    }

    @Command("npcconvert citizens")
    @Permission("fancynpcs.command.npcconvert.citizens")
    public void onCitizens(final CommandSender sender) {
        if (Bukkit.getPluginManager().getPlugin("Citizens") == null) {
            translator.translate("npcconvert_plugin_not_found")
                    .replace("source_plugin", "Citizens")
                    .send(sender);
            return;
        }

        NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
        int convertedCount = 0;
        for (NPC npc : npcRegistry) {
            Owner ownerTrait = npc.getTraitNullable(Owner.class);
            UUID ownerUUID = ownerTrait != null ? ownerTrait.getOwnerId() : null;

            String name = npc.getName().replaceAll(" ", "_");
            NpcData data = new NpcData(
                    name,
                    ownerUUID,
                    npc.getStoredLocation()
            );

            // type
            MobType mobTypeTrait = npc.getTraitNullable(MobType.class);
            if (mobTypeTrait != null) {
                data.setType(mobTypeTrait.getType());
            }

            // skin
            SkinTrait skinTrait = npc.getTraitNullable(SkinTrait.class);
            if (skinTrait != null && skinTrait.getSkinName() != null) {
                String skinName = skinTrait.getSkinName();
                data.setSkin(skinName);
            }

            // turn to player
            LookClose lookCloseTrait = npc.getTraitNullable(LookClose.class);
            if (lookCloseTrait != null && lookCloseTrait.isEnabled()) {
                data.setTurnToPlayer(true);
                data.setTurnToPlayerDistance((int) lookCloseTrait.getRange());
            }

            // glowing
            ScoreboardTrait scoreboardTrait = npc.getTraitNullable(ScoreboardTrait.class);
            if (scoreboardTrait != null && scoreboardTrait.getColor() != null) {
                data.setGlowing(true);
                data.setGlowingColor(NamedTextColor.NAMES.value(scoreboardTrait.getColor().name().toLowerCase()));
            }

            // equipment
            Equipment equipmentTrait = npc.getTraitNullable(Equipment.class);
            if (equipmentTrait != null) {
                for (Map.Entry<Equipment.EquipmentSlot, ItemStack> entry : equipmentTrait.getEquipmentBySlot().entrySet()) {
                    if (entry.getValue() == null) continue;

                    NpcEquipmentSlot fnSlot = switch (entry.getKey()) {
                        case HAND -> NpcEquipmentSlot.MAINHAND;
                        case OFF_HAND -> NpcEquipmentSlot.OFFHAND;
                        case BOOTS -> NpcEquipmentSlot.FEET;
                        case LEGGINGS -> NpcEquipmentSlot.LEGS;
                        case CHESTPLATE, BODY -> NpcEquipmentSlot.CHEST;
                        case HELMET -> NpcEquipmentSlot.HEAD;
                        default -> null;
                    };
                    if (fnSlot != null) {
                        data.addEquipment(fnSlot, entry.getValue().clone());
                    }
                }
            }

            // actions: message
            int actionIndex = 1;
            Text textTrait = npc.getTraitNullable(Text.class);
            if (textTrait != null) {
                NpcAction action = FancyNpcs.getInstance().getActionManager().getActionByName("message");
                for (String text : textTrait.getTexts()) {
                    data.addAction(
                            ActionTrigger.ANY_CLICK,
                            actionIndex++,
                            action,
                            text
                    );
                }
            }

            // actions: console command
            CommandTrait commandTrait = npc.getTraitNullable(CommandTrait.class);
            if (commandTrait != null) {
                NpcAction action = FancyNpcs.getInstance().getActionManager().getActionByName("console_command");
                Map<Integer, Object> commands = (Map<Integer, Object>) ReflectionUtils.getValue(commandTrait, "commands");
                for (Object commandObj : commands.values()) {
                    String command = (String) ReflectionUtils.getValue(commandObj, "command");

                    Enum<?> hand = (Enum<?>) ReflectionUtils.getValue(commandObj, "hand");
                    ActionTrigger trigger = switch (hand.name()) {
                        case "LEFT", "SHIT_LEFT" -> ActionTrigger.LEFT_CLICK;
                        case "RIGHT", "SHIFT_RIGHT" -> ActionTrigger.RIGHT_CLICK;
                        default -> ActionTrigger.ANY_CLICK;
                    };

                    data.addAction(
                            trigger,
                            actionIndex++,
                            action,
                            command
                    );
                }
            }

            // Register FancyNpcs npc
            Npc fnNpc = FancyNpcs.getInstance().getNpcAdapter().apply(data);
            fnNpc.create();
            FancyNpcs.getInstance().getNpcManager().registerNpc(fnNpc);
            convertedCount++;
        }

        if (convertedCount == 0) {
            translator.translate("npcconvert_no_npcs_found")
                    .replace("source_plugin", "Citizens")
                    .send(sender);
            return;
        }

        translator.translate("npcconvert_success")
                .replace("source_plugin", "Citizens")
                .replace("converted_count", String.valueOf(convertedCount))
                .send(sender);
    }
}
