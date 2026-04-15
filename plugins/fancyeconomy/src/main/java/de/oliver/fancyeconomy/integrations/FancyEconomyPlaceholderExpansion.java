package de.oliver.fancyeconomy.integrations;

import de.oliver.fancyeconomy.FancyEconomy;
import de.oliver.fancyeconomy.currencies.Currency;
import de.oliver.fancyeconomy.currencies.CurrencyPlayer;
import de.oliver.fancyeconomy.currencies.CurrencyPlayerManager;
import de.oliver.fancyeconomy.currencies.CurrencyRegistry;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FancyEconomyPlaceholderExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "FancyEconomy";
    }

    @Override
    public @NotNull String getAuthor() {
        return "OliverHD";
    }

    @Override
    public @NotNull String getVersion() {
        return FancyEconomy.getInstance().getDescription().getVersion();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(player.getUniqueId());
        Currency defaultCurrency = CurrencyRegistry.getDefaultCurrency();

        // %FancyEconomy_balance%
        if (params.equalsIgnoreCase("balance")) {
            return defaultCurrency.format(currencyPlayer.getBalance(defaultCurrency));
        }
        // %FancyEconomy_balance_raw%
        else if (params.equalsIgnoreCase("balance_raw")){
            return Currency.DECIMAL_FORMAT_RAW.format(currencyPlayer.getBalance(defaultCurrency));
        }

        // %FancyEconomy_balance_<currency>%
        for (Currency currency : CurrencyRegistry.CURRENCIES) {
            if (params.equalsIgnoreCase("balance_" + currency.name())) {
                return currency.format(currencyPlayer.getBalance(currency));
            } else if (params.equalsIgnoreCase("balance_raw_" + currency.name())){
                return Currency.DECIMAL_FORMAT_RAW.format(currencyPlayer.getBalance(currency));
            }
        }

        return null;
    }
}
