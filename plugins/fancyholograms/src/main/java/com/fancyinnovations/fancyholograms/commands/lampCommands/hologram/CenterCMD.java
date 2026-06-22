package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.DisplayHologramData;
import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import com.fancyinnovations.fancyholograms.util.Formats;
import de.oliver.fancylib.translations.Translator;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class CenterCMD {

    public static final CenterCMD INSTANCE = new CenterCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private CenterCMD() {
    }

    @Command("hologram-new edit <hologram> center")
    @Description("Moves the hologram to the center of the current block (x and z coords)")
    @CommandPermission("fancyholograms.commands.hologram.edit.center")
    public void set(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram
    ) {
        DisplayHologramData data = (DisplayHologramData) hologram.getData();

        Location currentLocation = data.getLocation();
        Location newLocation = currentLocation.clone().set(
                Math.floor(currentLocation.x()) + 0.5,
                currentLocation.y(),
                Math.floor(currentLocation.z()) + 0.5
        );

        DisplayHologramData copied = data.copy(data.getName());
        copied.setLocation(newLocation);

        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.POSITION)) {
            return;
        }

        data.setLocation(newLocation);

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.position.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("x", Formats.COORDINATES_DECIMAL.format(newLocation.x()))
                .replace("y", Formats.COORDINATES_DECIMAL.format(newLocation.y()))
                .replace("z", Formats.COORDINATES_DECIMAL.format(newLocation.z()))
                .send(actor.sender());
    }
}
