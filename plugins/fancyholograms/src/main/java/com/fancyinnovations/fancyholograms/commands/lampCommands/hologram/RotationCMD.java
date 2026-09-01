package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.DisplayHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.translations.Translator;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Range;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class RotationCMD {

    public static final RotationCMD INSTANCE = new RotationCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private RotationCMD() {
    }

    @Command("hologram-new edit <hologram> rotation yaw <angle>")
    @Description("Sets the yaw rotation of the hologram")
    @CommandPermission("fancyholograms.commands.hologram.rotation")
    public void yaw(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @Range(min = 0, max = 360) float angle
    ) {
        DisplayHologramData displayData = (DisplayHologramData) hologram.getData();

        // Set new angle
        Location loc = displayData.getLocation();
        loc.setYaw(angle - 180);

        // Create copy for event
        DisplayHologramData copied = displayData.copy(displayData.getName());
        copied.setLocation(loc);

        // Call modification event
        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.ROTATION)) {
            return;
        }

        // Apply modification
        displayData.setLocation(loc);

        // Auto-save if enabled
        if (plugin.getHologramConfiguration().isSaveOnChangedEnabled()) {
            plugin.getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.rotation.changed")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("axis", "yaw")
                .replace("angle", String.valueOf(angle))
                .send(actor.sender());
    }

    @Command("hologram-new edit <hologram> rotation pitch <angle>")
    @Description("Sets the pitch rotation of the hologram")
    @CommandPermission("fancyholograms.commands.hologram.rotation")
    public void pitch(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @Range(min = 0, max = 360) float angle
    ) {
        DisplayHologramData displayData = (DisplayHologramData) hologram.getData();

        // Set new angle
        Location loc = displayData.getLocation();
        loc.setPitch(angle);

        // Create copy for event
        DisplayHologramData copied = displayData.copy(displayData.getName());
        copied.setLocation(loc);

        // Call modification event
        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.ROTATION)) {
            return;
        }

        // Apply modification
        displayData.setLocation(loc);

        // Auto-save if enabled
        if (plugin.getHologramConfiguration().isSaveOnChangedEnabled()) {
            plugin.getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.rotation.changed")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("axis", "pitch")
                .replace("angle", String.valueOf(angle))
                .send(actor.sender());
    }
}
