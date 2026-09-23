package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram.edit;

import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.lampCommands.FancyContext;
import com.fancyinnovations.fancyholograms.util.PluginUtils;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class UnlinkWithNpcCMD extends FancyContext {

    public static final UnlinkWithNpcCMD INSTANCE = new UnlinkWithNpcCMD();

    private UnlinkWithNpcCMD() {
    }

    @Command({"hologram edit <hologram> unlink_with_npc", "hologram edit <hologram> unlink_npc"})
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

        if (config.isSaveOnChangedEnabled()) {
            plugin.getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.unlink_with_npc.success")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .send(actor.sender());
    }
}
