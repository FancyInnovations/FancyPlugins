package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.trait.HologramTraitRegistry;
import com.fancyinnovations.fancyholograms.commands.lampCommands.suggestions.AttachedTraitsSuggestion;
import com.fancyinnovations.fancyholograms.commands.lampCommands.suggestions.DetachedTraitsSuggestion;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.translations.Translator;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class TraitCMD {

    public static final TraitCMD INSTANCE = new TraitCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private TraitCMD() {
    }

    @Command("hologram-new edit <hologram> trait attach <trait>")
    @Description("Attaches a trait to a hologram")
    @CommandPermission("fancyholograms.commands.hologram.trait.attach")
    public void attach(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull @SuggestWith(AttachedTraitsSuggestion.class) HologramTraitRegistry.TraitInfo trait
    ) {
        if (hologram.getData().getTraitTrait().isTraitAttached(trait.clazz())) {
            translator.translate("commands.hologram.edit.trait.attach.already_attached")
                    .replace("hologram", hologram.getData().getName())
                    .replace("name", trait.name())
                    .send(actor.sender());
            return;
        }

        hologram.getData().addTrait(trait.clazz());

        translator.translate("commands.hologram.edit.trait.attach.success")
                .replace("hologram", hologram.getData().getName())
                .replace("name", trait.name())
                .send(actor.sender());
    }

    @Command("hologram-new edit <hologram> trait detach <trait>")
    @Description("Detaches a trait to a hologram")
    @CommandPermission("fancyholograms.commands.hologram.trait.detach")
    public void detach(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @SuggestWith(DetachedTraitsSuggestion.class) @NotNull HologramTraitRegistry.TraitInfo trait
    ) {
        if (!hologram.getData().getTraitTrait().isTraitAttached(trait.clazz())) {
            translator.translate("commands.hologram.edit.trait.detach.not_attached")
                    .replace("hologram", hologram.getData().getName())
                    .replace("name", trait.name())
                    .send(actor.sender());
            return;
        }

        hologram.getData().getTraitTrait().removeTrait(trait.clazz());

        translator.translate("commands.hologram.edit.trait.detach.success")
                .replace("hologram", hologram.getData().getName())
                .replace("name", trait.name())
                .send(actor.sender());
    }
}
