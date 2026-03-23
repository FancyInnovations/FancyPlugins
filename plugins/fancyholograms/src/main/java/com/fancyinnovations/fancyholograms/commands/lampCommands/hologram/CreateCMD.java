package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.BlockHologramData;
import com.fancyinnovations.fancyholograms.api.data.DisplayHologramData;
import com.fancyinnovations.fancyholograms.api.data.ItemHologramData;
import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramCreateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.translations.Translator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class CreateCMD {

    public static final CreateCMD INSTANCE = new CreateCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private CreateCMD() {
    }

    @Command("hologram-new create")
    @Description("Creates a new hologram")
    @CommandPermission("fancyholograms.commands.hologram.create")
    public void create(
            final BukkitCommandActor actor,
            String name,
            @Flag @Optional HologramType type
    ) {
        if (plugin.getRegistry().get(name).isPresent()) {
            translator.translate("commands.hologram.create.already_exists")
                    .replace("name", name)
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        if (name.contains(".")) {
            translator.translate("commands.hologram.create.no_dot")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        Location loc = actor.requirePlayer().getLocation();

        DisplayHologramData displayData = null;

        if (type == null) {
            type = HologramType.TEXT;
        }

        switch (type) {
            case TEXT -> displayData = new TextHologramData(name, loc);
            case ITEM -> {
                displayData = new ItemHologramData(name, loc);
                displayData.setBillboard(Display.Billboard.FIXED);
            }
            case BLOCK -> {
                displayData = new BlockHologramData(name, loc);
                displayData.setBillboard(Display.Billboard.FIXED);
            }
        }
        displayData.setFilePath(name);

        final var holo = FancyHologramsPlugin.get().getHologramFactory().apply(displayData);
        if (!new HologramCreateEvent(holo, actor.requirePlayer()).callEvent()) {
            translator.translate("commands.hologram.create.cancelled")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        FancyHologramsPlugin.get().getController().refreshHologram(holo, Bukkit.getOnlinePlayers());

        FancyHologramsPlugin.get().getRegistry().register(holo);

        translator.translate("commands.hologram.create.success")
                .withPrefix()
                .replace("name", name)
                .replace("type", type.name())
                .send(actor.sender());
    }

}
