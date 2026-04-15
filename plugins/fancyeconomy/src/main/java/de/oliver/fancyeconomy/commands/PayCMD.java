package de.oliver.fancyeconomy.commands;

import de.oliver.fancyeconomy.FancyEconomy;
import de.oliver.fancyeconomy.currencies.Currency;
import de.oliver.fancyeconomy.currencies.CurrencyPlayer;
import de.oliver.fancyeconomy.currencies.CurrencyPlayerManager;
import de.oliver.fancyeconomy.currencies.CurrencyRegistry;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.UUIDFetcher;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import dev.jorel.commandapi.annotations.arguments.ADoubleArgument;
import dev.jorel.commandapi.annotations.arguments.AStringArgument;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@Command("pay")
@Permission("fancyeconomy.pay")
public class PayCMD {

    @Default
    public static void info(CommandSender player) {
        MessageHelper.info(player, " --- FancyEconomy Info ---");
        MessageHelper.info(player, "/pay <player> <count> - Pay money to a certain player");
    }

    @Default
    public static void pay(
            Player player,
            @AStringArgument String targetName,
            @ADoubleArgument(min = 0.1) double amount
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

        Currency currency = CurrencyRegistry.getDefaultCurrency();

        CurrencyPlayer from = CurrencyPlayerManager.getPlayer(player.getUniqueId());
        CurrencyPlayer to = CurrencyPlayerManager.getPlayer(uuid);

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

        FancyEconomy.getInstance().getTranslator().translate("paid-sender")
                .replace("amount", currency.format(amount))
                .replace("receiver", to.getUsername())
                .send(player);

        if (targetPlayer != null) {
            FancyEconomy.getInstance().getTranslator().translate("paid-receiver")
                    .replace("amount", currency.format(amount))
                    .replace("sender", from.getUsername())
                    .send(targetPlayer);
        }
    }

}
