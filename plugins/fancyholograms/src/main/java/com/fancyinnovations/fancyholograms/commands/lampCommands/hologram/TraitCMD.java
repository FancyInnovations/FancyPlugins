package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.trait.HologramTrait;
import com.fancyinnovations.fancyholograms.api.trait.HologramTraitRegistry;
import com.fancyinnovations.fancyholograms.api.trait.HologramTraitTrait;
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
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("name", trait.name())
                    .send(actor.sender());
            return;
        }

        hologram.getData().addTrait(trait.clazz());

        translator.translate("commands.hologram.edit.trait.attach.success")
                .withPrefix()
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
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("name", trait.name())
                    .send(actor.sender());
            return;
        }

        hologram.getData().getTraitTrait().removeTrait(trait.clazz());

        translator.translate("commands.hologram.edit.trait.detach.success")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("name", trait.name())
                .send(actor.sender());
    }

    @Command("hologram-new edit <hologram> trait list")
    @Description("Lists all attached traits of a hologram")
    @CommandPermission("fancyholograms.commands.hologram.trait.list")
    public void list(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram
    ) {
        HologramTraitTrait traitTrait = hologram.getData().getTraitTrait();
        if (traitTrait.getTraits().isEmpty()) {
            translator.translate("commands.hologram.edit.trait.list.no_traits")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .send(actor.sender());
            return;
        }

        translator.translate("commands.hologram.edit.trait.list.header")
                .replace("hologram", hologram.getData().getName())
                .withPrefix()
                .send(actor.sender());

        for (HologramTrait trait : traitTrait.getTraits()) {
            translator.translate("commands.hologram.edit.trait.list.entry")
                    .replace("name", trait.getName())
                    .send(actor.sender());
        }
    }
}
