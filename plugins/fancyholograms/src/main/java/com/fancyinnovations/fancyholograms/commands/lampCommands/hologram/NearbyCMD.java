package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import com.fancyinnovations.fancyholograms.util.Formats;
import de.oliver.fancylib.translations.Translator;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class NearbyCMD {

    public static final NearbyCMD INSTANCE = new NearbyCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private NearbyCMD() {
    }

    @Command("hologram-new nearby <range>")
    @Description("Shows all holograms nearby in a radius")
    @CommandPermission("fancyholograms.commands.hologram.nearby")
    public void nearby(
            final @NotNull BukkitCommandActor actor,
            final @Optional @Default("10") @Range(min = 1) int range
    ) {
        Player player = actor.requirePlayer();
        Location playerLocation = player.getLocation().clone();

        List<Map.Entry<Hologram, Double>> nearby = plugin.getRegistry()
                .getAllPersistent()
                .stream()
                .filter(holo -> holo.getData().getWorldName() != null && holo.getData().getWorldName().equals(playerLocation.getWorld().getName()))
                .map(holo -> Map.entry(holo, holo.getData().getLocation().distance(playerLocation)))
                .filter(entry -> entry.getValue() <= range)
                .sorted(Comparator.comparingDouble(Map.Entry::getValue))
                .toList();

        if (nearby.isEmpty()) {
            translator.translate("commands.hologram.nearby.none_found")
                    .withPrefix()
                    .replace("range", String.valueOf(range))
                    .send(actor.sender());
            return;
        }

        translator.translate("commands.hologram.nearby.header")
                .withPrefix()
                .replace("range", String.valueOf(range))
                .send(actor.sender());

        nearby.forEach(entry -> {
            Hologram holo = entry.getKey();
            double distance = entry.getValue();

            final var location = holo.getData().getLocation();
            if (location == null || location.getWorld() == null) {
                return;
            }

            translator.translate("commands.hologram.nearby.entry")
                    .replace("name", holo.getData().getName())
                    .replace("x", Formats.COORDINATES_DECIMAL.format(location.x()))
                    .replace("y", Formats.COORDINATES_DECIMAL.format(location.y()))
                    .replace("z", Formats.COORDINATES_DECIMAL.format(location.z()))
                    .replace("world", location.getWorld().getName())
                    .replace("distance", Formats.COORDINATES_DECIMAL.format(distance))
                    .send(actor.sender());
        });
    }
}
