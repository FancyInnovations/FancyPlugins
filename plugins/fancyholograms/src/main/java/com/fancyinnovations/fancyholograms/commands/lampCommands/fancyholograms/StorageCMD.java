package com.fancyinnovations.fancyholograms.commands.lampCommands.fancyholograms;

import com.fancyinnovations.fancyholograms.api.data.HologramData;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.translations.Translator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class StorageCMD {

    public static final StorageCMD INSTANCE = new StorageCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private StorageCMD() {

    }

    @Command("fancyholograms storage save")
    @Description("Saves all holograms to the storage")
    @CommandPermission("fancyholograms.commands.fancyholograms.storage.save")
    public void save(
            final BukkitCommandActor actor
    ) {
        plugin.savePersistentHolograms();

        translator.translate("commands.fancyholograms.storage.save.success")
                .withPrefix()
                .send(actor.sender());
    }

    @Command("fancyholograms storage load")
    @Description("Loads all holograms from the storage")
    @CommandPermission("fancyholograms.commands.fancyholograms.storage.load")
    public void load(
            final BukkitCommandActor actor
    ) {
        Collection<Hologram> allHolograms = new ArrayList<>(plugin.getRegistry().getAll());

        plugin.getRegistry().clear();

        for (Hologram hologram : allHolograms) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                hologram.despawnFrom(player);
            }

            for (UUID viewer : hologram.getViewers()) {
                hologram.removeViewer(viewer);
            }
        }

        Collection<HologramData> hologramData = plugin.getStorage().loadAll();
        for (HologramData data : hologramData) {
            Hologram hologram = plugin.getHologramFactory().apply(data);
            plugin.getRegistry().register(hologram);
        }

        translator.translate("commands.fancyholograms.storage.load.success")
                .withPrefix()
                .send(actor.sender());
    }

}
