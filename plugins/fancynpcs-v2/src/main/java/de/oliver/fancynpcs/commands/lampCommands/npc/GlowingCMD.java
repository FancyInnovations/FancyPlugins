package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancylib.translations.message.SimpleMessage;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import de.oliver.fancynpcs.utils.GlowingColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class GlowingCMD extends FancyContext {
    public static final GlowingCMD INSTANCE = new GlowingCMD();

    private GlowingCMD() {
    }

    @Command("npc glowing <npc>")
    @CommandPermission("fancynpcs.command.npc.glowing")
    public void onGlowing(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final @Optional @Nullable GlowingColor color
    ) {
        final CommandSender sender = actor.sender();
        // Handling 'toggle' state, which means inverting the current state.
        if (color == null) {
            // Inverting the current glowing state, so the command works like a toggle.
            final boolean isGlowingToggled = !npc.getData().isGlowing();
            // Calling the event and updating the state if not cancelled.
            if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.GLOWING, isGlowingToggled, sender).callEvent()) {
                npc.getData().setGlowing(isGlowingToggled);
                npc.updateForAll();
                translator.translate(isGlowingToggled ? "npc_glowing_set_true" : "npc_glowing_set_false").withPrefix().replace("npc", npc.getData().getName()).send(sender);
            } else {
                translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
            }
        }
        // Handling 'disabled' state, which means disabling glowing state.
        else if (color == GlowingColor.DISABLED) {
            // Calling the event and updating the state if not cancelled.
            if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.GLOWING, false, sender).callEvent()) {
                npc.getData().setGlowing(false);
                npc.updateForAll();
                translator.translate("npc_glowing_set_false").withPrefix().replace("npc", npc.getData().getName()).send(sender);
            } else {
                translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
            }
            // Handling 'color' state, which means enabling glowing and changing the color to desired one.
        } else if (npc.getData().isGlowing() || new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.GLOWING, true, sender).callEvent()) {
            // Calling the event and updating the glowing color if not cancelled.
            if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.GLOWING_COLOR, color.getColor(), sender).callEvent()) {
                npc.getData().setGlowingColor(color.getColor());
                // Updating the glowing state, if previously disabled.
                if (!npc.getData().isGlowing())
                    npc.getData().setGlowing(true);
                npc.updateForAll();
                translator.translate("npc_glowing_set_color_success")
                        .withPrefix()
                        .replace("npc", npc.getData().getName())
                        .replace("color", ((SimpleMessage) translator.translate(color.getTranslationKey())).getMessage())
                        .send(sender);
            } else {
                translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
            }
        } else {
            translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
        }
    }

}
