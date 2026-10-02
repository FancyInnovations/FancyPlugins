package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.FancyHolograms;
import com.fancyinnovations.fancyholograms.api.events.HologramDeleteEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.lampCommands.FancyContext;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class RemoveCMD extends FancyContext {

    public static final RemoveCMD INSTANCE = new RemoveCMD();

    private RemoveCMD() {
    }

    @Command("hologram remove <hologram>")
    @Description("Removes a hologram")
    @CommandPermission("fancyholograms.commands.hologram.remove")
    public void remove(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram
    ) {
        if (!new HologramDeleteEvent(hologram, actor.sender()).callEvent()) {
            translator.translate("commands.hologram.remove.cancelled")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        FancyHolograms.get().getHologramThread().submit(() -> {
            plugin.getRegistry().unregister(hologram);
            translator.translate("commands.hologram.remove.success")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .send(actor.sender());
        });
    }
}
