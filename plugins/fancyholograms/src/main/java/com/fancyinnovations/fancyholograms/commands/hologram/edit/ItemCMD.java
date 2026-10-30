package com.fancyinnovations.fancyholograms.commands.hologram.edit;

import com.fancyinnovations.fancyholograms.api.data.ItemHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import com.fancyinnovations.fancyholograms.commands.FancyContext;
import com.fancyinnovations.fancyholograms.commands.HologramModification;
import com.fancyinnovations.fancyholograms.commands.conditions.IsHologramType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.UUID;

public final class ItemCMD extends FancyContext {

    public static final ItemCMD INSTANCE = new ItemCMD();

    private ItemCMD() {
    }

    @IsHologramType(types = {HologramType.ITEM})
    @Command("hologram edit <hologram> item")
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

        if (!HologramModification.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.ITEM)) {
            return;
        }

        itemData.setItemStack(item);

        for (UUID viewerUUID : hologram.getViewers()) {
            Player viewer = Bukkit.getPlayer(viewerUUID);
            if (viewer == null || !viewer.isOnline()) {
                continue;
            }

            hologram.despawnFrom(viewer);
            hologram.spawnTo(viewer);
        }

        if (config.isSaveOnChangedEnabled()) {
            plugin.getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.item.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("item", item.getType().name())
                .send(actor.sender());
    }
}
