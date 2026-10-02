package com.fancyinnovations.fancyeconomy.commands;

import com.fancyinnovations.fancyeconomy.FancyEconomy;
import com.fancyinnovations.fancyeconomy.currencies.*;
import de.oliver.fancylib.MessageHelper;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Range;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BalanceTopCMD {

    public static final int ENTRIES_PER_PAGE = 10;

    @Command({"balancetop", "baltop"})
    @CommandPermission("fancyeconomy.balancetop")
    public void balancetop(
            Player player,
            @Range(min = 1) @Default("1") int page
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
