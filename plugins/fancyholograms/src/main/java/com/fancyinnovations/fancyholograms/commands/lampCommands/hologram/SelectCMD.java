package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.translations.Translator;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Optional;

public final class SelectCMD {

    public static final SelectCMD INSTANCE = new SelectCMD();

    public static final NamespacedKey SELECTED_HOLOGRAM_KEY = new NamespacedKey(FancyHologramsPlugin.get(), "fancyholograms_selected_hologram");

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private SelectCMD() {
    }

    public static Optional<Hologram> getSelectedHologram(@NotNull Player player) {
        String hologramName = player.getPersistentDataContainer().get(SELECTED_HOLOGRAM_KEY, PersistentDataType.STRING);
        if (hologramName == null) {
            return Optional.empty();
        }

        return FancyHologramsPlugin.get().getRegistry().get(hologramName);
    }

    @Command("hologram select <hologram>")
    @Description("Selects a hologram, so you can edit it with other commands.")
    @CommandPermission("fancyholograms.commands.hologram.select")
    public void select(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram
    ) {
        Player player = actor.requirePlayer();

        player.getPersistentDataContainer().set(SELECTED_HOLOGRAM_KEY, PersistentDataType.STRING, hologram.getData().getName());

        translator.translate("commands.hologram.select.success")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .send(player);
    }
}
