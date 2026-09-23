package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram.edit;

import com.fancyinnovations.fancyholograms.api.data.HologramData;
import com.fancyinnovations.fancyholograms.api.data.property.Visibility;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.lampCommands.FancyContext;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class VisibilityCMD extends FancyContext {

    public static final VisibilityCMD INSTANCE = new VisibilityCMD();

    private VisibilityCMD() {
    }

    @Command("hologram edit <hologram> visibility <visibility>")
    @Description("Sets the visibility mode of the hologram")
    @CommandPermission("fancyholograms.commands.hologram.edit.visibility")
    public void set(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull Visibility visibility
    ) {
        HologramData data = hologram.getData();

        if (visibility == data.getVisibility()) {
            translator.translate("commands.hologram.edit.visibility.already_set")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("visibility", visibility.name())
                    .send(actor.sender());
            return;
        }

        data.setVisibility(visibility);

        if (config.isSaveOnChangedEnabled()) {
            plugin.getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.visibility.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("visibility", visibility.name())
                .send(actor.sender());
    }
}
