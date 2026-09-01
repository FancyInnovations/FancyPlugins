package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import com.fancyinnovations.fancyholograms.util.PluginUtils;
import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class UnlinkWithNpcCMD {

    public static final UnlinkWithNpcCMD INSTANCE = new UnlinkWithNpcCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private UnlinkWithNpcCMD() {
    }

    @Command({"hologram-new edit <hologram> unlink_with_npc", "hologram-new edit <hologram> unlink_npc"})
    @Description("Unlinks the hologram from an NPC")
    @CommandPermission("fancyholograms.commands.hologram.edit.unlink_with_npc")
    public void unlink(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram
    ) {
        if (!PluginUtils.isFancyNpcsEnabled()) {
            translator.translate("commands.hologram.edit.link_with_npc.fancynpcs_not_installed")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        if (hologram.getData().getLinkedNpcName() == null) {
            translator.translate("commands.hologram.edit.unlink_with_npc.not_linked")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .send(actor.sender());
            return;
        }

        final Npc npc = FancyNpcsPlugin.get().getNpcManager().getNpc(hologram.getData().getLinkedNpcName());

        hologram.getData().setLinkedNpcName(null);

        if (npc != null) {
            npc.getData().setDisplayName(npc.getData().getName());
            npc.updateForAll();
        }

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.unlink_with_npc.success")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .send(actor.sender());
    }
}
