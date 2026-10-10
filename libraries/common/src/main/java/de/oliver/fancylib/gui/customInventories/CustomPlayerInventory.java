package de.oliver.fancylib.gui.customInventories;

import de.oliver.fancylib.gui.inventoryClick.InventoryItemClick;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public abstract class CustomPlayerInventory implements InventoryHolder {

    protected final Player player;
    protected final Inventory inventory;

    protected CustomPlayerInventory(Player player, int amountRows, Component title) {
        this.player = player;
        this.inventory = Bukkit.createInventory(this, amountRows * 9, title);
    }

    public static ItemStack getPlaceholder() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        item.editMeta(itemMeta -> {
            itemMeta.displayName(Component.empty());
            itemMeta.getPersistentDataContainer().set(InventoryItemClick.ON_CLICK_KEY, PersistentDataType.STRING, "cancelClick");
        });

        return item;
    }

    public Player getPlayer() {
        return player;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void open() {
        player.openInventory(inventory);
    }
}
