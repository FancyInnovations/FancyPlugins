package de.oliver.fancynpcs.commands.lampCommands.types;

import de.oliver.fancynpcs.api.utils.NpcEquipmentSlot;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public final class EquipmentSlotCommandType implements ParameterType<BukkitCommandActor, NpcEquipmentSlot> {

    public static final EquipmentSlotCommandType INSTANCE = new EquipmentSlotCommandType();

    private EquipmentSlotCommandType() {
    }

    @Override
    public NpcEquipmentSlot parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<BukkitCommandActor> context) {
        String value = input.readString().toLowerCase();
        NpcEquipmentSlot slot = NpcEquipmentSlot.parse(value);

        if (slot == null) {
            throw new FancyNpcsParameterException("command_invalid_equipment_slot", value);
        }
        return slot;
    }

    @Override
    public SuggestionProvider<BukkitCommandActor> defaultSuggestions() {
        return context -> java.util.Arrays.stream(NpcEquipmentSlot.values()).map(slot -> slot.name().toLowerCase()).toList();
    }
}
