package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram.edit;

import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import com.fancyinnovations.fancyholograms.commands.lampCommands.FancyContext;
import com.fancyinnovations.fancyholograms.commands.lampCommands.conditions.IsHologramType;
import com.fancyinnovations.fancyholograms.commands.oldCommands.HologramCMD;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class AddLineCMD extends FancyContext {

    public static final AddLineCMD INSTANCE = new AddLineCMD();

    private AddLineCMD() {
    }

    @IsHologramType(types = {HologramType.TEXT})
    @Command("hologram edit <hologram> add_line <text>")
    @Description("Adds a line to the hologram")
    @CommandPermission("fancyholograms.commands.hologram.edit.add_line")
    public void addLine(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull String text
    ) {
        TextHologramData textData = (TextHologramData) hologram.getData();

        final var copied = textData.copy(textData.getName());
        copied.addLine(text);

        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.TEXT)) {
            return;
        }

        textData.addLine(text);

        if (config.isSaveOnChangedEnabled()) {
            plugin.getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.lines.added")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .send(actor.sender());
    }
}
