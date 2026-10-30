package com.fancyinnovations.fancyholograms.commands;

import com.fancyinnovations.fancyholograms.api.data.HologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class HologramModification {

    public static boolean callModificationEvent(@NotNull final Hologram hologram, @NotNull final CommandSender player, @NotNull final HologramData updatedData, @NotNull final HologramUpdateEvent.HologramModification modification) {
        boolean result = new HologramUpdateEvent(hologram, player, updatedData, modification).callEvent();

        if (!result) {
            MessageHelper.error(player, "Cancelled hologram modification");
        }

        return result;
    }

}
