package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancynpcs.api.AttributeManager;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import de.oliver.fancynpcs.commands.lampCommands.types.AttributeValueCommandType;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.ParseWith;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class AttributeCMD extends FancyContext {
    public static final AttributeCMD INSTANCE = new AttributeCMD();
    private final AttributeManager attributeManager = plugin.getAttributeManager();

    private AttributeCMD() {
    }

    @Command("npc attribute <npc> set <attribute> <attributeValue>")
    @CommandPermission("fancynpcs.command.npc.attribute.set")
    public void onAttributeSet(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final @NotNull NpcAttribute attribute,
            final @NotNull @Named("attributeValue") @ParseWith(AttributeValueCommandType.class) String attributeValue
    ) {
        final CommandSender sender = actor.sender();
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.ATTRIBUTE, new Object[]{attribute, attributeValue}, sender).callEvent()) {
            npc.getData().addAttribute(attribute, attributeValue);
            npc.updateForAll();
            translator.translate("npc_attribute_set")
                    .withPrefix()
                    .replace("attribute", attribute.getName())
                    .replaceStripped("value", attributeValue.toLowerCase())
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled")
                    .withPrefix()
                    .send(sender);
        }
    }

    @Command("npc attribute <npc> list")
    @CommandPermission("fancynpcs.command.npc.attribute.list")
    public void onAttributeList(
            final BukkitCommandActor actor,
            final @NotNull Npc npc
    ) {
        final CommandSender sender = actor.sender();
        // Sending error message if the list is empty.
        if (npc.getData().getAttributes().isEmpty()) {
            translator.translate("npc_attribute_list_failure_empty")
                    .withPrefix()
                    .send(sender);
            return;
        }
        translator.translate("npc_attribute_list_header")
                .send(sender);
        // Iterating over all attributes set on this NPC and sending them to the sender.
        npc.getData().getAttributes().forEach((attribute, value) -> {
            translator.translate("npc_attribute_list_entry")
                    .replace("attribute", attribute.getName())
                    .replace("value", value)
                    .send(sender);
        });
        translator.translate("npc_attribute_list_footer").send(sender);
    }


}
