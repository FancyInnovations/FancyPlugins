package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.api.utils.Interval;
import de.oliver.fancynpcs.api.utils.Interval.Unit;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.regex.Pattern;

public final class InteractionCooldownCMD extends FancyContext {
    public static final InteractionCooldownCMD INSTANCE = new InteractionCooldownCMD();
    private static final Pattern SPLIT_PATTERN = Pattern.compile("(?<=\\d)(?=\\D)");

    private InteractionCooldownCMD() {
    }

    @Command("npc interaction_cooldown <npc> <cooldown>")
    @CommandPermission("fancynpcs.command.npc.interaction_cooldown")
    public void onInteractionCooldown(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final @NotNull Interval cooldown
    ) {
        final CommandSender sender = actor.sender();
        // Calling the event and updating the cooldown if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.INTERACTION_COOLDOWN, cooldown, sender).callEvent()) {
            npc.getData().setInteractionCooldown((float) cooldown.as(Unit.MILLISECONDS) / 1000F);
            translator.translate(cooldown.as(Unit.MILLISECONDS) != 0 ? "npc_interaction_cooldown_set" : "npc_interaction_cooldown_disabled")
                    .withPrefix()
                    .replace("npc", npc.getData().getName())
                    .replace("time", cooldown.toString())
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
        }
    }

    /* UTILITY METHODS */

    private @Nullable Long parseLong(final @NotNull String value) {
        try {
            return Long.parseLong(value);
        } catch (final NumberFormatException e) {
            return null;
        }
    }

}
