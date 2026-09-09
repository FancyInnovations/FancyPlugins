package com.fancyinnovations.fancyholograms.commands.lampCommands.traits;

import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.lampCommands.conditions.HasHologramTrait;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import com.fancyinnovations.fancyholograms.trait.builtin.FileContentTrait;
import de.oliver.fancylib.translations.Translator;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class FileContentTraitCMD {

    public static final FileContentTraitCMD INSTANCE = new FileContentTraitCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private FileContentTraitCMD() {

    }

    @Command("hologramtrait file_content <hologram> info")
    @Description("Displays information about the file content trait for the hologram")
    @CommandPermission("fancyholograms.commands.hologramtrait.file_content.info")
    @HasHologramTrait(FileContentTrait.class)
    public void info(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram
    ) {
        final FileContentTrait trait = hologram.getData().getTraitTrait().getTrait(FileContentTrait.class);
        assert trait != null;

        translator.translate("commands.hologramtrait.file_content.info.header")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .send(actor.sender());

        translator.translate("commands.hologramtrait.file_content.info.file_path")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("file_path", trait.getConfig().filePath())
                .send(actor.sender());

        translator.translate("commands.hologramtrait.file_content.info.refresh_interval")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("refresh_interval", String.valueOf(trait.getConfig().refreshInterval()))
                .send(actor.sender());
    }

    @Command("hologramtrait file_content <hologram> file_path <file_path>")
    @Description("Sets the file path for the file content trait of the hologram")
    @CommandPermission("fancyholograms.commands.hologramtrait.file_content.file_path")
    @HasHologramTrait(FileContentTrait.class)
    public void setFilePath(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull String file_path
    ) {
        final FileContentTrait trait = hologram.getData().getTraitTrait().getTrait(FileContentTrait.class);
        assert trait != null;

        trait.setFilePath(file_path);

        translator.translate("commands.hologramtrait.file_content.file_path.success")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("file_path", file_path)
                .send(actor.sender());
    }

    @Command("hologramtrait file_content <hologram> refresh_interval <refresh_interval>")
    @Description("Sets the refresh interval for the file content trait of the hologram")
    @CommandPermission("fancyholograms.commands.hologramtrait.file_content.refresh_interval")
    @HasHologramTrait(FileContentTrait.class)
    public void setRefreshInterval(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final long refresh_interval
    ) {
        final FileContentTrait trait = hologram.getData().getTraitTrait().getTrait(FileContentTrait.class);
        assert trait != null;

        trait.setRefreshInterval(refresh_interval);

        translator.translate("commands.hologramtrait.file_content.refresh_interval.success")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("refresh_interval", String.valueOf(refresh_interval))
                .send(actor.sender());
    }

    @Command("hologramtrait file_content <hologram> update")
    @Description("Updates the file content for the file content trait of the hologram")
    @CommandPermission("fancyholograms.commands.hologramtrait.file_content.update")
    @HasHologramTrait(FileContentTrait.class)
    public void update(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram
    ) {
        final FileContentTrait trait = hologram.getData().getTraitTrait().getTrait(FileContentTrait.class);
        assert trait != null;

        trait.updateHologram();

        translator.translate("commands.hologramtrait.file_content.update.success")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .send(actor.sender());
    }

}
