package de.oliver.fancyeconomy.currencies;

import de.oliver.fancyeconomy.FancyEconomy;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.gui.inventoryClick.InventoryItemClick;
import de.oliver.fancylib.itemClick.ItemClick;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public record Currency(String name, String symbol, boolean isWithdrawable, WithdrawItem withdrawItem) {

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###,###,###.##");
    public static final DecimalFormat DECIMAL_FORMAT_RAW = new DecimalFormat("##################.##");

    public String format(double amount) {
        return FancyEconomy.getInstance().getFancyEconomyConfig().useShortFormat()
                ? shortFormat(amount) + " " + name
                : longFormat(amount) + " " + name;
    }

    private String longFormat(double amount) {
        return symbol + DECIMAL_FORMAT.format(amount);
    }

    private String shortFormat(double amount) {
        DecimalFormat formatter = new DecimalFormat("#.##");
        if (amount >= 1.0E18D) {
            return formatter.format(amount / 1.0E18D) + "QT";
        }
        if (amount >= 1.0E15D) {
            return formatter.format(amount / 1.0E15D) + "Q";
        }
        if (amount >= 1.0E12D) {
            return formatter.format(amount / 1.0E12D) + "T";
        }
        if (amount >= 1.0E9D) {
            return formatter.format(amount / 1.0E9D) + "B";
        }
        if (amount >= 1000000.0D) {
            return formatter.format(amount / 1000000.0D) + "M";
        }
        if (amount < 1000.0D) {
            return formatter.format(amount);
        }
        return formatter.format(amount / 1000.0D) + "k";
    }

    public record WithdrawItem(Material material, String displayName, List<String> lore) {

        public static final NamespacedKey WITHDRAW_OWNER = new NamespacedKey(FancyEconomy.getInstance(), "withdraw_owner");
        public static final NamespacedKey WITHDRAW_CURRENCY = new NamespacedKey(FancyEconomy.getInstance(), "withdraw_currency");
        public static final NamespacedKey WITHDRAW_AMOUNT = new NamespacedKey(FancyEconomy.getInstance(), "withdraw_amount");

        public ItemStack construct(Player player, Currency currency, double amount) {
            ItemStack item = new ItemStack(material);

            item.editMeta(meta -> {
                meta.displayName(MessageHelper.removeDecoration(MiniMessage.miniMessage().deserialize(displayName
                        .replace("%player%", player.getName())
                        .replace("%currency%", currency.name())
                        .replace("%amount%", currency.format(amount))
                ), TextDecoration.ITALIC));

                meta.lore(lore.stream()
                        .map(line -> MessageHelper.removeDecoration(MiniMessage.miniMessage().deserialize(line
                                .replace("%player%", player.getName())
                                .replace("%currency%", currency.name())
                                .replace("%amount%", currency.format(amount))
                        ), TextDecoration.ITALIC))
                        .toList()
                );

                meta.getPersistentDataContainer().set(ItemClick.ON_CLICK_KEY, PersistentDataType.STRING, WithdrawItemClick.INSTANCE.getId());
                meta.getPersistentDataContainer().set(WITHDRAW_OWNER, PersistentDataType.STRING, player.getUniqueId().toString());
                meta.getPersistentDataContainer().set(WITHDRAW_CURRENCY, PersistentDataType.STRING, currency.name());
                meta.getPersistentDataContainer().set(WITHDRAW_AMOUNT, PersistentDataType.DOUBLE, amount);
            });

            return item;
        }

        public static class WithdrawItemClick implements ItemClick {

            public static WithdrawItemClick INSTANCE = new WithdrawItemClick();

            private static List<NamespacedKey> REQUIRED_KEYS = Arrays.asList(
                    WITHDRAW_OWNER,
                    WITHDRAW_CURRENCY,
                    WITHDRAW_AMOUNT
            );

            private WithdrawItemClick() {
            }

            @Override
            public String getId() {
                return "fancyeconomy_withdraw";
            }

            @Override
            public void onClick(PlayerInteractEvent event, Player player) {
                ItemStack item = event.getItem();
                if (item == null || !InventoryItemClick.hasKeys(item, REQUIRED_KEYS)) {
                    return;
                }

                event.setCancelled(true);

                String currencyName = item.getItemMeta().getPersistentDataContainer().get(WITHDRAW_CURRENCY, PersistentDataType.STRING);
                Currency currency = CurrencyRegistry.getCurrencyByName(currencyName);
                if (currency == null) {
                    return;
                }

                double amount = item.getItemMeta().getPersistentDataContainer().get(WITHDRAW_AMOUNT, PersistentDataType.DOUBLE);

                CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(player.getUniqueId());
                currencyPlayer.setUsername(player.getName());

                currencyPlayer.addBalance(currency, amount);

                item.setAmount(item.getAmount() - 1);
                FancyEconomy.getInstance().getTranslator()
                        .translate("deposit-note")
                        .replace("amount", currency.format(amount))
                        .send(player);
            }
        }

    }

}
