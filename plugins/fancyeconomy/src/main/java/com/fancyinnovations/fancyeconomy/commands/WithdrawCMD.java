package com.fancyinnovations.fancyeconomy.commands;

import com.fancyinnovations.fancyeconomy.FancyEconomy;
import com.fancyinnovations.fancyeconomy.currencies.Currency;
import com.fancyinnovations.fancyeconomy.currencies.CurrencyPlayer;
import com.fancyinnovations.fancyeconomy.currencies.CurrencyPlayerManager;
import com.fancyinnovations.fancyeconomy.currencies.CurrencyRegistry;
import de.oliver.fancylib.MessageHelper;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class WithdrawCMD {

    @Command("withdraw")
    @CommandPermission("fancyeconomy.withdraw")
    public void info(CommandSender player) {
        MessageHelper.info(player, " --- FancyEconomy Info ---");
        MessageHelper.info(player, "/withdraw <count> - Withdraw a certain amount of money");
    }

    @Command("withdraw")
    @CommandPermission("fancyeconomy.withdraw")
    public void withdraw(
            Player player,
            double amount
    ) {
        Currency currency = CurrencyRegistry.getDefaultCurrency();

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
}
