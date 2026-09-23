package de.oliver.fancynpcs.commands.lampCommands;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.api.utils.Interval;
import de.oliver.fancynpcs.api.utils.NpcEquipmentSlot;
import de.oliver.fancynpcs.commands.lampCommands.npc.*;
import de.oliver.fancynpcs.commands.lampCommands.types.*;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

/**
 * Registers FancyNpcs commands and their Lamp parameter types.
 */
public final class LampCommandManager {

    private final Lamp<BukkitCommandActor> lamp;

    public LampCommandManager(FancyNpcs plugin) {
        Lamp.Builder<BukkitCommandActor> builder = BukkitLamp.builder(plugin);
        builder.parameterTypes(types -> {
            types.addParameterType(Npc.class, NpcCommandType.INSTANCE);
            types.addParameterType(NpcAction.class, ActionTypeCommandType.INSTANCE);
            types.addParameterType(ActionTrigger.class, ActionTriggerCommandType.INSTANCE);
            types.addParameterType(Location.class, LocationCommandType.INSTANCE);
            types.addParameterType(NpcAttribute.class, AttributeCommandType.INSTANCE);
            types.addParameterType(NpcEquipmentSlot.class, EquipmentSlotCommandType.INSTANCE);
            types.addParameterType(Interval.class, IntervalCommandType.INSTANCE);
            types.addParameterType(ItemStack.class, ItemStackCommandType.INSTANCE);
        });

        builder.exceptionHandler(new FancyNpcsExceptionHandler(plugin));

        lamp = builder.build();

        registerCommands();
    }

    private void registerCommands() {
        lamp.register(FancyNpcsCMD.INSTANCE);
        lamp.register(NpcConvertCMD.INSTANCE);
        lamp.register(AttributeCMD.INSTANCE);
        lamp.register(CenterCMD.INSTANCE);
        lamp.register(CollidableCMD.INSTANCE);
        lamp.register(CopyCMD.INSTANCE);
        lamp.register(CreateCMD.INSTANCE);
        lamp.register(DisplayNameCMD.INSTANCE);
        lamp.register(EquipmentCMD.INSTANCE);
        lamp.register(FixCMD.INSTANCE);
        lamp.register(GlowingCMD.INSTANCE);
        lamp.register(InfoCMD.INSTANCE);
        lamp.register(InteractionCooldownCMD.INSTANCE);
        lamp.register(ListCMD.INSTANCE);
        lamp.register(MoveHereCMD.INSTANCE);
        lamp.register(MoveToCMD.INSTANCE);
        lamp.register(NearbyCMD.INSTANCE);
        lamp.register(HelpCMD.INSTANCE);
        lamp.register(RemoveCMD.INSTANCE);
        lamp.register(RotateCMD.INSTANCE);
        lamp.register(ShowInTabCMD.INSTANCE);
        lamp.register(SkinCMD.INSTANCE);
        lamp.register(TeleportCMD.INSTANCE);
        lamp.register(TurnToPlayerCMD.INSTANCE);
        lamp.register(TurnToPlayerDistanceCMD.INSTANCE);
        lamp.register(TypeCMD.INSTANCE);
        lamp.register(ActionCMD.INSTANCE);
        lamp.register(VisibilityDistanceCMD.INSTANCE);
        lamp.register(VisibilityCMD.INSTANCE);
        lamp.register(ScaleCMD.INSTANCE);
        
        if (FancyNpcs.ENABLE_DEBUG_MODE_FEATURE_FLAG.isEnabled()) {
            lamp.register(FancyNpcsDebugCMD.INSTANCE);
        }
    }

    public Lamp<BukkitCommandActor> getLamp() {
        return lamp;
    }

    public void register(Object command) {
        lamp.register(command);
    }
}
