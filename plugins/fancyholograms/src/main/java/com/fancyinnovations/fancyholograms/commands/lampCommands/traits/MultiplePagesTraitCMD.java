package com.fancyinnovations.fancyholograms.commands.lampCommands.traits;

import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.lampCommands.conditions.HasHologramTrait;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import com.fancyinnovations.fancyholograms.trait.builtin.MultiplePagesTrait;
import de.oliver.fancylib.duration.FancyDuration;
import de.oliver.fancylib.translations.Translator;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Range;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.ArrayList;
import java.util.List;

public final class MultiplePagesTraitCMD {

    public static final MultiplePagesTraitCMD INSTANCE = new MultiplePagesTraitCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private MultiplePagesTraitCMD() {
    }

    @Command("hologramtrait multiple_pages <hologram> info")
    @Description("Displays information about the multiple pages trait for the hologram")
    @CommandPermission("fancyholograms.commands.hologramtrait.multiple_pages.info")
    @HasHologramTrait(MultiplePagesTrait.class)
    public void info(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram
    ) {
        final MultiplePagesTrait trait = hologram.getData().getTraitTrait().getTrait(MultiplePagesTrait.class);
        assert trait != null;

        translator.translate("commands.hologramtrait.multiple_pages.info.header")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .send(actor.sender());

        translator.translate("commands.hologramtrait.multiple_pages.info.mode")
                .replace("mode", trait.getMode().name())
                .send(actor.sender());

        translator.translate("commands.hologramtrait.multiple_pages.info.delay")
                .replace("delay", new FancyDuration(trait.getCycleDelay()).toString())
                .send(actor.sender());

        translator.translate("commands.hologramtrait.multiple_pages.info.current_index")
                .replace("index", String.valueOf(trait.getCurrentPageIndex() + 1))
                .send(actor.sender());

        String pages = "";
        for (int i = 0; i < trait.getPages().size(); i++) {
            String lines = String.join("\n", trait.getPages().get(i).lines());

            pages += "<hover:show_text:'" + lines + "'>[" + (i + 1) + "]</hover> ";
        }

        translator.translate("commands.hologramtrait.multiple_pages.info.pages")
                .replace("pages", pages)
                .send(actor.sender());
    }

    @Command("hologramtrait multiple_pages <hologram> mode <mode>")
    @Description("Sets the mode of the multiple pages trait for the hologram")
    @CommandPermission("fancyholograms.commands.hologramtrait.multiple_pages.mode")
    @HasHologramTrait(MultiplePagesTrait.class)
    public void mode(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull MultiplePagesTrait.Mode mode
    ) {
        final MultiplePagesTrait trait = hologram.getData().getTraitTrait().getTrait(MultiplePagesTrait.class);
        assert trait != null;

        trait.setMode(mode);

        translator.translate("commands.hologramtrait.multiple_pages.mode.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("mode", mode.name())
                .send(actor.sender());
    }

    @Command("hologramtrait multiple_pages <hologram> delay <delay>")
    @Description("Sets the delay between page cycles for the hologram")
    @CommandPermission("fancyholograms.commands.hologramtrait.multiple_pages.delay")
    @HasHologramTrait(MultiplePagesTrait.class)
    public void delay(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final FancyDuration delay
    ) {
        final MultiplePagesTrait trait = hologram.getData().getTraitTrait().getTrait(MultiplePagesTrait.class);
        assert trait != null;

        trait.setCycleDelay(delay.millis());

        translator.translate("commands.hologramtrait.multiple_pages.delay.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("delay", delay.toString())
                .send(actor.sender());
    }

    @Command("hologramtrait multiple_pages <hologram> current_index <index>")
    @Description("Sets the current index of the page for the hologram")
    @CommandPermission("fancyholograms.commands.hologramtrait.multiple_pages.current_index")
    @HasHologramTrait(MultiplePagesTrait.class)
    public void currentIndex(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @Range(min = 1) int index
    ) {
        final MultiplePagesTrait trait = hologram.getData().getTraitTrait().getTrait(MultiplePagesTrait.class);
        assert trait != null;

        if (index < 1 || index > trait.getPages().size()) {
            translator.translate("commands.hologramtrait.multiple_pages.current_index.invalid_index")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("index", String.valueOf(index))
                    .replace("min", "1")
                    .replace("max", String.valueOf(trait.getPages().size()))
                    .send(actor.sender());
            return;
        }

        trait.setCurrentPageIndex(index - 1); // Adjusting for 0-based index

        translator.translate("commands.hologramtrait.multiple_pages.current_index.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("index", String.valueOf(index))
                .send(actor.sender());
    }

    @Command("hologramtrait multiple_pages <hologram> add_line <page> <text>")
    @Description("Adds a line to the specified page of the hologram")
    @CommandPermission("fancyholograms.commands.hologramtrait.multiple_pages.add_line")
    @HasHologramTrait(MultiplePagesTrait.class)
    public void addLine(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @Range(min = 1) int page,
            final @NotNull String text
    ) {
        final MultiplePagesTrait trait = hologram.getData().getTraitTrait().getTrait(MultiplePagesTrait.class);
        assert trait != null;

        MultiplePagesTrait.Page p = trait.getPages().get(page - 1);
        if (p == null) {
            translator.translate("commands.hologramtrait.multiple_pages.page_not_found")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("page", String.valueOf(page))
                    .send(actor.sender());
            return;
        }

        List<String> newLines = new ArrayList<>(p.lines());
        newLines.add(text);

        trait.setLines(page - 1, newLines);

        translator.translate("commands.hologramtrait.multiple_pages.add_line.success")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("page", String.valueOf(page))
                .send(actor.sender());
    }

    @Command("hologramtrait multiple_pages <hologram> remove_line <page> <index>")
    @Description("Removes a line from the specified page of the hologram")
    @CommandPermission("fancyholograms.commands.hologramtrait.multiple_pages.remove_line")
    @HasHologramTrait(MultiplePagesTrait.class)
    public void removeLine(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @Range(min = 1) int page,
            final @Range(min = 1) int index
    ) {
        final MultiplePagesTrait trait = hologram.getData().getTraitTrait().getTrait(MultiplePagesTrait.class);
        assert trait != null;

        MultiplePagesTrait.Page p = trait.getPages().get(page - 1);
        if (p == null) {
            translator.translate("commands.hologramtrait.multiple_pages.page_not_found")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("page", String.valueOf(page))
                    .send(actor.sender());
            return;
        }

        List<String> newLines = new ArrayList<>(p.lines());
        if (index < 1 || index > newLines.size()) {
            translator.translate("commands.hologramtrait.multiple_pages.line_not_found")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("page", String.valueOf(page))
                    .replace("index", String.valueOf(index))
                    .send(actor.sender());
            return;
        }

        newLines.remove(index - 1);
        trait.setLines(page - 1, newLines);

        translator.translate("commands.hologramtrait.multiple_pages.remove_line.success")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("page", String.valueOf(page))
                .replace("index", String.valueOf(index))
                .send(actor.sender());
    }

    @Command("hologramtrait multiple_pages <hologram> set_line <page> <index> <text>")
    @Description("Sets the text of a line on the specified page of the hologram")
    @CommandPermission("fancyholograms.commands.hologramtrait.multiple_pages.set_line")
    @HasHologramTrait(MultiplePagesTrait.class)
    public void setLine(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @Range(min = 1) int page,
            final @Range(min = 1) int index,
            final @NotNull String text
    ) {
        final MultiplePagesTrait trait = hologram.getData().getTraitTrait().getTrait(MultiplePagesTrait.class);
        assert trait != null;

        MultiplePagesTrait.Page p = trait.getPages().get(page - 1);
        if (p == null) {
            translator.translate("commands.hologramtrait.multiple_pages.page_not_found")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("page", String.valueOf(page))
                    .send(actor.sender());
            return;
        }

        List<String> newLines = new ArrayList<>(p.lines());
        if (index < 1 || index > newLines.size()) {
            translator.translate("commands.hologramtrait.multiple_pages.line_not_found")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("page", String.valueOf(page))
                    .replace("index", String.valueOf(index))
                    .send(actor.sender());
            return;
        }

        newLines.set(index - 1, text);
        trait.setLines(page - 1, newLines);

        translator.translate("commands.hologramtrait.multiple_pages.set_line.success")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("page", String.valueOf(page))
                .replace("index", String.valueOf(index))
                .send(actor.sender());
    }
}
