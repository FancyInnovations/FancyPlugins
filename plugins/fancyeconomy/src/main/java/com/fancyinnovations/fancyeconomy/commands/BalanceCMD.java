package com.fancyinnovations.fancyeconomy.commands;

import com.fancyinnovations.fancyeconomy.FancyEconomy;
import com.fancyinnovations.fancyeconomy.currencies.Currency;
import com.fancyinnovations.fancyeconomy.currencies.CurrencyPlayer;
import com.fancyinnovations.fancyeconomy.currencies.CurrencyPlayerManager;
import com.fancyinnovations.fancyeconomy.currencies.CurrencyRegistry;
import de.oliver.fancylib.UUIDFetcher;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BalanceCMD {

    @Command({"balance", "bal"})
    @CommandPermission("fancyeconomy.balance")
    public void balance(Player player) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(player.getUniqueId());
        Currency currency = CurrencyRegistry.getDefaultCurrency();
        double balance = currencyPlayer.getBalance(currency);

        FancyEconomy.getInstance().getTranslator()
                .translate("your-balance")
                .replace("balance", currency.format(balance))
                .send(player);
    }

    @Command({"balance", "bal"})
    @CommandPermission("fancyeconomy.balance.others")
    public void balance(
            Player player,
            @SuggestWith(AllPlayersSuggestion.class) String targetName
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

        Currency currency = CurrencyRegistry.getDefaultCurrency();
        double balance = currencyPlayer.getBalance(currency);

        FancyEconomy.getInstance().getTranslator()
                .translate("balance-others")
                .replace("player", currencyPlayer.getUsername())
                .replace("balance", currency.format(balance))
                .send(player);
    }

}
