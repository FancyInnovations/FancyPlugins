package de.oliver.fancyeconomy.currencies;

import java.util.ArrayList;
import java.util.List;

public class CurrencyRegistry {

    public static final List<Currency> CURRENCIES = new ArrayList<>();
    private static Currency defaultCurrency;

    public static void registerCurrency(Currency currency) {
        CURRENCIES.add(currency);

        if (defaultCurrency == null) {
            defaultCurrency = currency;
        }
    }

    public static Currency getCurrencyByName(String name) {
        for (Currency currency : CURRENCIES) {
            if (currency.name().equalsIgnoreCase(name)) {
                return currency;
            }
        }

        return null;
    }

    public static Currency getDefaultCurrency() {
        return defaultCurrency;
    }

    public static void setDefaultCurrency(Currency defaultCurrency) {
        CurrencyRegistry.defaultCurrency = defaultCurrency;
    }
}
