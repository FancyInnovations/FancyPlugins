package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.commands.lampCommands.conditions.IsHologramType;
import com.fancyinnovations.fancyholograms.commands.lampCommands.types.ColorCommandType;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.translations.Translator;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class BillboardCMD {

    public static final BillboardCMD INSTANCE = new BillboardCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private BillboardCMD() {
    }

    @Command("hologram-new edit <hologram> billboard <billboard>")
    @Description("Changes the billboard of the hologram")
    @CommandPermission("fancyholograms.commands.hologram.edit.billboard")
    public void set(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull Display.Billboard billboard
    ) {
        TextHologramData data = (TextHologramData) hologram.getData();

        TextHologramData copied = data.copy(data.getName());
        copied.setBillboard(billboard);

        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.BILLBOARD)) {
            return;
        }

        if (copied.getBillboard().equals(data.getBillboard())) {
            translator.translate("commands.hologram.edit.billboard.already_set")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("billboard", billboard.name())
                    .send(actor.sender());
            return;
        }

        data.setBillboard(billboard);

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.billboard.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("billboard", billboard.name())
                .send(actor.sender());
    }
}
