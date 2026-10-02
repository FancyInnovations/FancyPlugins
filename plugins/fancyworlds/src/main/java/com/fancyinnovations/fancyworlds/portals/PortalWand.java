package com.fancyinnovations.fancyworlds.portals;

import com.fancyinnovations.fancyworlds.main.FancyWorldsPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class PortalWand {

    private final NamespacedKey wandKey;

    public PortalWand(FancyWorldsPlugin plugin) {
        this.wandKey = new NamespacedKey(plugin, "portal_wand");
    }

    public ItemStack createItem() {
        ItemStack item = new ItemStack(Material.GOLDEN_AXE);
        ItemMeta meta = item.getItemMeta();
        meta.itemName(Component.text("FancyWorlds Portal Wand", TextColor.color(0xffcc24)));
        meta.lore(List.of(
                loreLine("Left-click to set the first position"),
                loreLine("Right-click to set the second position")
        ));
        meta.getPersistentDataContainer().set(wandKey, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isWand(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }

        Byte value = item.getItemMeta().getPersistentDataContainer().get(wandKey, PersistentDataType.BYTE);
        return value != null && value == (byte) 1;
    }

    private TextComponent loreLine(String text) {
        return Component.text(text, NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false);
    }
}
