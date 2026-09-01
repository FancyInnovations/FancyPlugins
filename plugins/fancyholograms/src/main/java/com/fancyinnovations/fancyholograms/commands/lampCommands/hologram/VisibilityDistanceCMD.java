package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.HologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.translations.Translator;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Range;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class VisibilityDistanceCMD {

    public static final VisibilityDistanceCMD INSTANCE = new VisibilityDistanceCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private VisibilityDistanceCMD() {
    }

    @Command("hologram-new edit <hologram> visibility_distance <distance>")
    @Description("Changes the visibility distance of the hologram")
    @CommandPermission("fancyholograms.commands.hologram.edit.visibility_distance")
    public void set(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @Range(min = -1) int distance
    ) {
        HologramData data = hologram.getData();

        if (distance == data.getVisibilityDistance()) {
            translator.translate("commands.hologram.edit.visibility_distance.already_set")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .send(actor.sender());
            return;
        }

        final var copied = data.copy(data.getName());
        copied.setVisibilityDistance(distance);

        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.UPDATE_VISIBILITY_DISTANCE)) {
            return;
        }

        data.setVisibilityDistance(copied.getVisibilityDistance());

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.visibility_distance.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("distance", String.valueOf(distance))
                .send(actor.sender());
    }
}
