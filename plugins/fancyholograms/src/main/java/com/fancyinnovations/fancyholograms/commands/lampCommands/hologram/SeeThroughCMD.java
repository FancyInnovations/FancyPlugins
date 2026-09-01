package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.commands.lampCommands.conditions.IsHologramType;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.translations.Translator;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class SeeThroughCMD {

    public static final SeeThroughCMD INSTANCE = new SeeThroughCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private SeeThroughCMD() {
    }

    @IsHologramType(types = {HologramType.TEXT})
    @Command("hologram-new edit <hologram> see_through <enabled>")
    @Description("Enables or disables whether the hologram text can be seen through blocks")
    @CommandPermission("fancyholograms.commands.hologram.edit.see_through")
    public void set(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final boolean enabled
    ) {
        TextHologramData textData = (TextHologramData) hologram.getData();

        if (enabled == textData.isSeeThrough()) {
            translator.translate("commands.hologram.edit.see_through.already_set")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("enabled", enabled ? "enabled" : "disabled")
                    .send(actor.sender());
            return;
        }

        final var copied = textData.copy(textData.getName());
        copied.setSeeThrough(enabled);

        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.SEE_THROUGH)) {
            return;
        }

        textData.setSeeThrough(copied.isSeeThrough());

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.see_through.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("enabled", enabled ? "enabled" : "disabled")
                .send(actor.sender());
    }
}
