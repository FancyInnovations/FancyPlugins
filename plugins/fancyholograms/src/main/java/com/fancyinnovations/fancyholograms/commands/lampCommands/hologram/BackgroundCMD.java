package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.DisplayHologramData;
import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.commands.lampCommands.conditions.IsHologramType;
import com.fancyinnovations.fancyholograms.commands.lampCommands.types.ColorCommandType;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.colors.GlowingColor;
import de.oliver.fancylib.translations.Translator;
import de.oliver.fancylib.translations.message.SimpleMessage;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class BackgroundCMD {

    public static final BackgroundCMD INSTANCE = new BackgroundCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private BackgroundCMD() {
    }

    @IsHologramType(types = {HologramType.TEXT})
    @Command("hologram-new edit <hologram> background <color>")
    @Description("Changes the background color of the hologram")
    @CommandPermission("fancyholograms.commands.hologram.edit.background")
    public void set(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @Nullable Color color
    ) {
        TextHologramData data = (TextHologramData) hologram.getData();

        TextHologramData copied = data.copy(data.getName());
        copied.setBackground(color);

        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.BACKGROUND)) {
            return;
        }

        if (copied.getBackground().equals(data.getBackground())) {
            translator.translate("commands.hologram.edit.background.already_set")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("color", ColorCommandType.toString(color))
                    .send(actor.sender());
            return;
        }

        data.setBackground(color);

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.background.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("color", ColorCommandType.toString(color))
                .send(actor.sender());
    }
}
