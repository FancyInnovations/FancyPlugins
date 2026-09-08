package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import com.fancyinnovations.fancyholograms.util.Formats;
import de.oliver.fancylib.translations.Translator;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Collection;

public final class ListCMD {

    public static final ListCMD INSTANCE = new ListCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private ListCMD() {
    }

    @Command({"hologram list"})
    @Description("Shows a list of all holograms")
    @CommandPermission("fancyholograms.commands.hologram.list")
    public void list(
            final @NotNull BukkitCommandActor actor,
            final @Optional @Default("1") Integer page
    ) {
        Collection<Hologram> holograms = plugin.getRegistry().getAllPersistent();

        if (holograms.isEmpty()) {
            translator.translate("commands.hologram.list.empty")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        int actualPage = (page == null || page < 1) ? 1 : page;
        int totalPages = (holograms.size() + 9) / 10;

        if (actualPage > totalPages) {
            translator.translate("commands.hologram.list.page_out_of_bounds")
                    .withPrefix()
                    .replace("page", String.valueOf(actualPage))
                    .replace("max", String.valueOf(totalPages))
                    .send(actor.sender());
            return;
        }

        translator.translate("commands.hologram.list.header")
                .withPrefix()
                .replace("count", String.valueOf(holograms.size()))
                .send(actor.sender());

        translator.translate("commands.hologram.list.page")
                .replace("page", String.valueOf(actualPage))
                .replace("pages", String.valueOf(totalPages))
                .send(actor.sender());

        holograms.stream()
                .skip((actualPage - 1) * 10L)
                .limit(10)
                .forEach(holo -> {
                    Location location = holo.getData().getLocation();
                    if (holo.getData().getWorldName() == null) {
                        return;
                    }
                    translator.translate("commands.hologram.list.entry")
                            .replace("name", holo.getData().getName())
                            .replace("type", holo.getData().getType().name())
                            .replace("x", Formats.COORDINATES_DECIMAL.format(location.x()))
                            .replace("y", Formats.COORDINATES_DECIMAL.format(location.y()))
                            .replace("z", Formats.COORDINATES_DECIMAL.format(location.z()))
                            .replace("world", holo.getData().getWorldName())
                            .send(actor.sender());
                });
    }
}
