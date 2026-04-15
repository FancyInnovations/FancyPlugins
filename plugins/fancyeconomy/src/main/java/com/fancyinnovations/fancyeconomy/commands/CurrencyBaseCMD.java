package com.fancyinnovations.fancyeconomy.commands;

import com.fancyinnovations.fancyeconomy.FancyEconomy;
import com.fancyinnovations.fancyeconomy.currencies.BalanceTop;
import com.fancyinnovations.fancyeconomy.currencies.Currency;
import com.fancyinnovations.fancyeconomy.currencies.CurrencyPlayer;
import com.fancyinnovations.fancyeconomy.currencies.CurrencyPlayerManager;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class CurrencyBaseCMD {

    private final Currency currency;

    public CurrencyBaseCMD(Currency currency) {
        this.currency = currency;
    }

    public void info(Player player) {
        MessageHelper.info(player, " --- FancyEconomy Help ---");
        FancyEconomy.getInstance().getTranslator()
                .translate("help-balance")
                .replace("currency", currency.name())
                .send(player);

        FancyEconomy.getInstance().getTranslator()
                .translate("help-balance-others")
                .replace("currency", currency.name())
                .send(player);

        FancyEconomy.getInstance().getTranslator()
                .translate("help-pay")
                .replace("currency", currency.name())
                .send(player);

        FancyEconomy.getInstance().getTranslator()
                .translate("help-withdraw")
                .replace("currency", currency.name())
                .send(player);

        FancyEconomy.getInstance().getTranslator()
                .translate("help-top")
                .replace("currency", currency.name())
                .send(player);

        if (player.hasPermission("fancyeconomy." + currency.name() + ".admin")) {
            FancyEconomy.getInstance().getTranslator()
                    .translate("help-set")
                    .replace("currency", currency.name())
                    .send(player);

            FancyEconomy.getInstance().getTranslator()
                    .translate("help-add")
                    .replace("currency", currency.name())
                    .send(player);

            FancyEconomy.getInstance().getTranslator()
                    .translate("help-remove")
                    .replace("currency", currency.name())
                    .send(player);
        }
    }

    public void balance(Player player) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(player.getUniqueId());
        double balance = currencyPlayer.getBalance(currency);

        FancyEconomy.getInstance().getTranslator()
                .translate("your-balance")
                .replace("balance", currency.format(balance))
                .send(player);
    }

    public void balance(
            Player player,
            String targetName
    ) {
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer != null) {
            targetName = targetPlayer.getName();
        }

        UUID uuid = targetPlayer != null ? targetPlayer.getUniqueId() : UUIDFetcher.getUUID(targetName);

        if (uuid == null) {
            FancyEconomy.getInstance().getTranslator()
                    .translate("player-not-found")
                    .replace("player", targetName)
                    .send(player);

            return;
        }

        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(uuid);

        if (targetPlayer != null) {
            currencyPlayer.setUsername(targetPlayer.getName());
        }

        double balance = currencyPlayer.getBalance(currency);

        FancyEconomy.getInstance().getTranslator()
                .translate("balance-others")
                .replace("player", currencyPlayer.getUsername())
                .replace("balance", currency.format(balance))
                .send(player);
    }

    public void pay(
            Player player,
            String targetName,
            double amount
    ) {
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer != null) {
            targetName = targetPlayer.getName();
        }

        UUID uuid = targetPlayer != null ? targetPlayer.getUniqueId() : UUIDFetcher.getUUID(targetName);

        if (uuid == null) {
            FancyEconomy.getInstance().getTranslator()
                    .translate("player-not-found")
                    .replace("player", targetName)
                    .send(player);
            return;
        }

        if (player.getUniqueId().equals(uuid)) {
            FancyEconomy.getInstance().getTranslator()
                    .translate("cannot-pay-yourself")
                    .send(player);
            return;
        }

        CurrencyPlayer from = CurrencyPlayerManager.getPlayer(player.getUniqueId());
        CurrencyPlayer to = CurrencyPlayerManager.getPlayer(uuid);
        from.setUsername(player.getName());

        if (targetPlayer != null) {
            to.setUsername(targetPlayer.getName());
        }

        boolean allowNegativeBalance = FancyEconomy.getInstance().getFancyEconomyConfig().allowNegativeBalance();
        if (!allowNegativeBalance && from.getBalance(currency) < amount) {
            FancyEconomy.getInstance().getTranslator()
                    .translate("not-enough-money")
                    .send(player);
            return;
        }

        double maxNegativeBalance = FancyEconomy.getInstance().getFancyEconomyConfig().getMaxNegativeBalance();
        if (allowNegativeBalance && from.getBalance(currency) - maxNegativeBalance < amount) {
            FancyEconomy.getInstance().getTranslator()
                    .translate("not-enough-money")
                    .send(player);
            return;
        }

        from.removeBalance(currency, amount);
        to.addBalance(currency, amount);

        FancyEconomy.getInstance().getTranslator()
                .translate("paid-sender")
                .replace("amount", currency.format(amount))
                .replace("receiver", to.getUsername())
                .send(player);

        if (targetPlayer != null) {
            FancyEconomy.getInstance().getTranslator()
                    .translate("paid-receiver")
                    .replace("amount", currency.format(amount))
                    .replace("sender", from.getUsername())
                    .send(targetPlayer);
        }
    }

    public void withdraw(
            Player player,
            double amount
    ) {
        if (!currency.isWithdrawable()) {
            FancyEconomy.getInstance().getTranslator()
                    .translate("not-withdrawable")
                    .send(player);
            return;
        }

        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(player.getUniqueId());
        currencyPlayer.setUsername(player.getName());

        double minWithdrawAmount = FancyEconomy.getInstance().getFancyEconomyConfig().getMinWithdrawAmount();
        double maxWithdrawAmount = FancyEconomy.getInstance().getFancyEconomyConfig().getMaxWithdrawAmount();

        if (amount < minWithdrawAmount) {
            FancyEconomy.getInstance().getTranslator()
                    .translate("min-withdrawable")
                    .replace("amount", currency.format(minWithdrawAmount))
                    .send(player);
            return;
        }

        if (amount > maxWithdrawAmount) {
            FancyEconomy.getInstance().getTranslator()
                    .translate("max-withdrawable")
                    .replace("amount", currency.format(maxWithdrawAmount))
                    .send(player);
            return;
        }

        boolean allowNegativeBalance = FancyEconomy.getInstance().getFancyEconomyConfig().allowNegativeBalance();
        if (!allowNegativeBalance && currencyPlayer.getBalance(currency) < amount) {
            FancyEconomy.getInstance().getTranslator()
                    .translate("not-enough-money")
                    .send(player);
            return;
        }

        double maxNegativeBalance = FancyEconomy.getInstance().getFancyEconomyConfig().getMaxNegativeBalance();
        if (allowNegativeBalance && currencyPlayer.getBalance(currency) - maxNegativeBalance < amount) {
            FancyEconomy.getInstance().getTranslator()
                    .translate("not-enough-money")
                    .send(player);
            return;
        }

        ItemStack withdrawItem = currency.withdrawItem().construct(player, currency, amount);

        HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(withdrawItem);
        if (leftOver.size() > 0) {
            FancyEconomy.getInstance().getTranslator()
                    .translate("no-inventory-space")
                    .send(player);
            return;
        }

        currencyPlayer.removeBalance(currency, amount);

        FancyEconomy.getInstance().getTranslator()
                .translate("withdraw-success")
                .replace("amount", currency.format(amount))
                .send(player);
    }

    public void balancetop(Player player) {
        balancetop(player, 1);
    }

    public void balancetop(
            Player player,
            int page
    ) {
        BalanceTop balanceTop = BalanceTop.getForCurrency(currency);

        if ((page - 1) * BalanceTopCMD.ENTRIES_PER_PAGE > balanceTop.getAmountEntries()) {
            FancyEconomy.getInstance().getTranslator()
                    .translate("balance-top-empty-page")
                    .send(player);
            return;
        }

        MessageHelper.info(player, "<b>Balance top: " + currency.name() + "</b> <gray>(Page #" + page + ")");

        for (int i = 1; i <= BalanceTopCMD.ENTRIES_PER_PAGE; i++) {
            final int place = (page - 1) * BalanceTopCMD.ENTRIES_PER_PAGE + i;
            UUID uuid = balanceTop.getAtPlace(place);
            if (uuid == null) {
                break;
            }

            CurrencyPlayer cp = CurrencyPlayerManager.getPlayer(uuid);
            MessageHelper.info(player, place + ". " + cp.getUsername() + " <gray>(" + currency.format(cp.getBalance(currency)) + ")");
        }

        int yourPlace = balanceTop.getPlayerPlace(player.getUniqueId());
        FancyEconomy.getInstance().getTranslator()
                .translate("balancetop-your-place")
                .replace("place", yourPlace > 0 ? String.valueOf(yourPlace) : "N/A")
                .send(player);
    }

    public void set(
            Player player,
            String targetName,
            double amount
    ) {
        if (!player.hasPermission("fancyeconomy." + currency.name() + ".admin")) {
            FancyEconomy.getInstance().getTranslator()
                    .translate("no-permissions")
                    .send(player);
            return;
        }

        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer != null) {
            targetName = targetPlayer.getName();
        }

        UUID uuid = targetPlayer != null ? targetPlayer.getUniqueId() : UUIDFetcher.getUUID(targetName);

        if (uuid == null) {
            FancyEconomy.getInstance().getTranslator()
                    .translate("player-not-found")
                    .replace("player", targetName)
                    .send(player);
            return;
        }

        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(uuid);
        currencyPlayer.setBalance(currency, amount);

        FancyEconomy.getInstance().getTranslator()
                .translate("set-success")
                .replace("player", currencyPlayer.getUsername())
                .replace("amount", currency.format(amount))
                .send(player);
    }

    public void add(
            Player player,
            String targetName,
            double amount
    ) {
        if (!player.hasPermission("fancyeconomy." + currency.name() + ".admin")) {
            FancyEconomy.getInstance().getTranslator()
                    .translate("no-permissions")
                    .send(player);
            return;
        }

        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer != null) {
            targetName = targetPlayer.getName();
        }

        UUID uuid = targetPlayer != null ? targetPlayer.getUniqueId() : UUIDFetcher.getUUID(targetName);

        if (uuid == null) {
            FancyEconomy.getInstance().getTranslator()
                    .translate("player-not-found")
                    .replace("player", targetName)
                    .send(player);
            return;
        }

        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(uuid);
        currencyPlayer.addBalance(currency, amount);

        FancyEconomy.getInstance().getTranslator()
                .translate("add-success")
                .replace("player", currencyPlayer.getUsername())
                .replace("amount", currency.format(amount))
                .send(Bukkit.getPlayer(uuid));
    }

    public void remove(
            Player player,
            String targetName,
            double amount
    ) {
        if (!player.hasPermission("fancyeconomy." + currency.name() + ".admin")) {
            FancyEconomy.getInstance().getTranslator()
                    .translate("no-permissions")
                    .send(player);
            return;
        }

        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer != null) {
            targetName = targetPlayer.getName();
        }

        UUID uuid = targetPlayer != null ? targetPlayer.getUniqueId() : UUIDFetcher.getUUID(targetName);

        if (uuid == null) {
            FancyEconomy.getInstance().getTranslator()
                    .translate("player-not-found")
                    .replace("player", targetName)
                    .send(player);
            return;
        }

        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(uuid);
        currencyPlayer.removeBalance(currency, amount);

        FancyEconomy.getInstance().getTranslator()
                .translate("remove-success")
                .replace("player", currencyPlayer.getUsername())
                .replace("amount", currency.format(amount))
                .send(Bukkit.getPlayer(uuid));
    }
}
