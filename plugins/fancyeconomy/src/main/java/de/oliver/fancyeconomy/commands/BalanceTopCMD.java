package de.oliver.fancyeconomy.commands;

import de.oliver.fancyeconomy.FancyEconomy;
import de.oliver.fancyeconomy.currencies.*;
import de.oliver.fancylib.MessageHelper;
import dev.jorel.commandapi.annotations.Alias;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import dev.jorel.commandapi.annotations.arguments.AIntegerArgument;
import org.bukkit.entity.Player;

import java.util.UUID;

@Command("balancetop")
@Permission("fancyeconomy.balancetop")
@Alias({"baltop"})
public class BalanceTopCMD {

    public static final int ENTRIES_PER_PAGE = 10;

    @Default
    public static void info(Player player) {
        MessageHelper.info(player, " --- FancyEconomy Info ---");
        MessageHelper.info(player, "/balancetop - Shows the richest players");
        MessageHelper.info(player, "/balancetop <page> - Shows the richest players");
    }

    @Default
    public static void balancetop(Player player) {
        balancetop(player, 1);
    }

    @Default
    public static void balancetop(
            Player player,
            @AIntegerArgument(min = 1) int page
    ) {
        Currency currency = CurrencyRegistry.getDefaultCurrency();

        BalanceTop balanceTop = BalanceTop.getForCurrency(currency);

        if ((page - 1) * ENTRIES_PER_PAGE > balanceTop.getAmountEntries()) {
            FancyEconomy.getInstance().getTranslator()
                    .translate("balance-top-empty-page")
                    .send(player);
            return;
        }

        MessageHelper.info(player, "<b>Balance top: " + currency.name() + "</b> <gray>(Page #" + page + ")");

        for (int i = 1; i <= ENTRIES_PER_PAGE; i++) {
            final int place = (page - 1) * ENTRIES_PER_PAGE + i;
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

}
