package com.fancyinnovations.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import com.fancyinnovations.fancynpcs.FancyNpcs;
import com.fancyinnovations.fancynpcs.api.Npc;
import com.fancyinnovations.fancynpcs.api.events.NpcModifyEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ShowInTabCMD {
    INSTANCE; // SINGLETON

    // Mannequin support detection (1.21.9+) - Mannequin entities don't use tab list
    private static final boolean MANNEQUIN_SUPPORTED;
    static {
        boolean supported = false;
        try {
            EntityType.valueOf("MANNEQUIN");
            supported = true;
        } catch (IllegalArgumentException e) {
            // Mannequin not available on this version
        }
        MANNEQUIN_SUPPORTED = supported;
    }

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc show_in_tab <npc> [state]")
    @Permission("fancynpcs.command.npc.show_in_tab")
    public void onCommand(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @Nullable Boolean state
    ) {
        // Only PLAYER type NPCs can be shown in tab, and only when not using Mannequin
        // Mannequin entities (1.21.9+) don't use the tab list at all
        if (npc.getData().getType() != EntityType.PLAYER) {
            translator.translate("npc_show_in_tab_not_supported")
                    .replace("type", npc.getData().getType().name())
                    .send(sender);
            return;
        }

        // On 1.21.9+, PLAYER type uses Mannequin which doesn't support tab list
        if (MANNEQUIN_SUPPORTED) {
            translator.translate("npc_show_in_tab_not_supported_mannequin").send(sender);
            return;
        }

        final boolean finalState = (state == null) ? !npc.getData().isShowInTab() : state;
        // Calling the event and updating the state if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SHOW_IN_TAB, finalState, sender).callEvent()) {
            npc.getData().setShowInTab(finalState);
            npc.removeForAll();
            npc.create();
            npc.spawnForAll();
            translator.translate(finalState ? "npc_show_in_tab_set_true" : "npc_show_in_tab_set_false").replace("npc", npc.getData().getName()).send(sender);
            return;
        }
        // Otherwise, sending error message to the sender.
        translator.translate("command_npc_modification_cancelled").send(sender);
    }

}
