package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.lampCommands.suggestions.NpcNameSuggestion;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import com.fancyinnovations.fancyholograms.util.PluginUtils;
import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class LinkWithNpcCMD {

    public static final LinkWithNpcCMD INSTANCE = new LinkWithNpcCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private LinkWithNpcCMD() {
    }

    @Command({"hologram-new edit <hologram> link_with_npc <npc>", "hologram-new edit <hologram> link_npc <npc>"})
    @Description("Links the hologram with an NPC")
    @CommandPermission("fancyholograms.commands.hologram.edit.link_with_npc")
    public void link(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull @SuggestWith(NpcNameSuggestion.class) String npc
    ) {
        if (!PluginUtils.isFancyNpcsEnabled()) {
            translator.translate("commands.hologram.edit.link_with_npc.fancynpcs_not_installed")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        if (hologram.getData().getLinkedNpcName() != null) {
            translator.translate("commands.hologram.edit.link_with_npc.already_linked")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("npc", hologram.getData().getLinkedNpcName())
                    .send(actor.sender());
            return;
        }

        final Npc foundNpc = FancyNpcsPlugin.get().getNpcManager().getNpc(npc);
        if (foundNpc == null) {
            translator.translate("commands.hologram.edit.link_with_npc.npc_not_found")
                    .withPrefix()
                    .replace("name", npc)
                    .send(actor.sender());
            return;
        }

        hologram.getData().setLinkedNpcName(foundNpc.getData().getName());

        plugin.getControllerImpl().syncHologramWithNpc(hologram);

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.link_with_npc.success")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("npc", foundNpc.getData().getName())
                .send(actor.sender());
    }
}
