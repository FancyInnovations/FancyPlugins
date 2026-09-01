package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.ItemHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.commands.lampCommands.conditions.IsHologramType;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.translations.Translator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class ItemCMD {

    public static final ItemCMD INSTANCE = new ItemCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private ItemCMD() {
    }

    @IsHologramType(types = {HologramType.ITEM})
    @Command("hologram-new edit <hologram> item")
    @Description("Sets the item of the hologram to the item in your main hand")
    @CommandPermission("fancyholograms.commands.hologram.edit.item")
    public void set(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram
    ) {
        Player player = actor.requirePlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR || item.getAmount() < 1) {
            translator.translate("commands.hologram.edit.item.hold_item")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        ItemHologramData itemData = (ItemHologramData) hologram.getData();

        if (item.equals(itemData.getItemStack())) {
            translator.translate("commands.hologram.edit.item.already_set")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("item", item.getType().name())
                    .send(actor.sender());
            return;
        }

        final var copied = itemData.copy(itemData.getName());
        copied.setItemStack(item);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.BILLBOARD)) {
            return;
        }

        itemData.setItemStack(item);

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.item.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("item", item.getType().name())
                .send(actor.sender());
    }
}
