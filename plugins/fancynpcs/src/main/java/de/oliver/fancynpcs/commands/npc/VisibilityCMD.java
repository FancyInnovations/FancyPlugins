package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.data.property.NpcVisibility;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import org.jetbrains.annotations.NotNull;

public enum VisibilityCMD {
    INSTANCE;

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc visibility <npc> <visibility>")
    @Permission("fancynpcs.command.npc.visibility")
    public void onVisibility(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull NpcVisibility visibility
    ) {
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.VISIBILITY, visibility, sender).callEvent()) {
            npc.getData().setVisibility(visibility);

            npc.checkAndUpdateVisibilityForAll();

            translator.translate("npc_visibility_set")
                    .replace("npc", npc.getData().getName())
                    .replace("visibility", visibility.toString())
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }
}