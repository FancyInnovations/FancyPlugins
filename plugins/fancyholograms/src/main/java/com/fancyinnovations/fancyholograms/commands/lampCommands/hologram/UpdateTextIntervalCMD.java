package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.commands.lampCommands.conditions.IsHologramType;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.duration.FancyDuration;
import de.oliver.fancylib.translations.Translator;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class UpdateTextIntervalCMD {

    public static final UpdateTextIntervalCMD INSTANCE = new UpdateTextIntervalCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private UpdateTextIntervalCMD() {
    }

    @IsHologramType(types = HologramType.TEXT)
    @Command("hologram-new edit <hologram> update_text_interval")
    @Description("The interval between the hologram text updates (useful when using placeholders)")
    @CommandPermission("fancyholograms.commands.hologram.update_text_interval")
    public void updateTextInterval(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull FancyDuration duration
    ) {
        TextHologramData data = (TextHologramData) hologram.getData();

        TextHologramData copied = data.copy(data.getName());
        copied.setTextUpdateInterval((int) duration.millis());

        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.UPDATE_TEXT_INTERVAL)) {
            return;
        }

        if (copied.getTextUpdateInterval() == data.getTextUpdateInterval()) {
            translator.translate("commands.hologram.edit.update_text_interval.already_set")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("interval", duration.toString())
                    .send(actor.sender());
            return;
        }

        data.setTextUpdateInterval(copied.getTextUpdateInterval());

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        if (duration.isNever()) {
            translator.translate("commands.hologram.edit.update_text_interval.disabled")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("interval", duration.toString())
                    .send(actor.sender());
            return;
        }

        translator.translate("commands.hologram.edit.update_text_interval.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("interval", duration.toString())
                .send(actor.sender());

        if (duration.millis() < 1000) {
            translator.translate("commands.hologram.edit.update_text_interval.short_interval_warning")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("interval", duration.toString())
                    .send(actor.sender());
        }
    }

}
