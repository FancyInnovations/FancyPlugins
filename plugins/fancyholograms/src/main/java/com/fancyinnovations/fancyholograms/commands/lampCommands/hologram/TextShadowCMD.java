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

public final class TextShadowCMD {

    public static final TextShadowCMD INSTANCE = new TextShadowCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private TextShadowCMD() {
    }

    @IsHologramType(types = {HologramType.TEXT})
    @Command("hologram-new edit <hologram> text_shadow <enabled>")
    @Description("Enables or disables the text shadow of the hologram")
    @CommandPermission("fancyholograms.commands.hologram.edit.text_shadow")
    public void set(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final boolean enabled
    ) {
        TextHologramData textData = (TextHologramData) hologram.getData();

        if (enabled == textData.hasTextShadow()) {
            translator.translate("commands.hologram.edit.text_shadow.already_set")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("enabled", enabled ? "enabled" : "disabled")
                    .send(actor.sender());
            return;
        }

        final var copied = textData.copy(textData.getName());
        copied.setTextShadow(enabled);

        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.TEXT_SHADOW)) {
            return;
        }

        textData.setTextShadow(copied.hasTextShadow());

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.text_shadow.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("enabled", enabled ? "enabled" : "disabled")
                .send(actor.sender());
    }
}
