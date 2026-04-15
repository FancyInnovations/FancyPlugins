package de.oliver.fancyeconomy.integrations;

import de.oliver.fancyeconomy.FancyEconomy;
import de.oliver.fancyeconomy.currencies.Currency;
import de.oliver.fancyeconomy.currencies.CurrencyPlayer;
import de.oliver.fancyeconomy.currencies.CurrencyPlayerManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;

public class FancyEconomyVault implements Economy {

    private final Currency currency;

    public FancyEconomyVault(Currency currency) {
        this.currency = currency;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "FancyEconomy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double v) {
        return currency.format(v);
    }

    @Override
    public String currencyNamePlural() {
        return currency.name();
    }

    @Override
    public String currencyNameSingular() {
        return currency.name();
    }

    @Override
    public boolean hasAccount(String s) {
        return true;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return true;
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        return true;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return true;
    }

    @Override
    public double getBalance(String s) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(s);
        return currencyPlayer != null ? currencyPlayer.getBalance(currency) : 0d;
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(offlinePlayer.getUniqueId());
        return currencyPlayer != null ? currencyPlayer.getBalance(currency) : 0d;
    }

    @Override
    public double getBalance(String s, String s1) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(s);
        return currencyPlayer != null ? currencyPlayer.getBalance(currency) : 0d;
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(offlinePlayer.getUniqueId());
        return currencyPlayer != null ? currencyPlayer.getBalance(currency) : 0d;
    }

    @Override
    public boolean has(String s, double v) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(s);
        double amount = currencyPlayer != null ? currencyPlayer.getBalance(currency) : 0d;

        return amount >= v;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(offlinePlayer.getUniqueId());
        double amount = currencyPlayer != null ? currencyPlayer.getBalance(currency) : 0d;

        return amount >= v;
    }

    @Override
    public boolean has(String s, String s1, double v) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(s);
        double amount = currencyPlayer != null ? currencyPlayer.getBalance(currency) : 0d;

        return amount >= v;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(offlinePlayer.getUniqueId());
        double amount = currencyPlayer != null ? currencyPlayer.getBalance(currency) : 0d;

        return amount >= v;
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(s);
        if (currencyPlayer == null) {
            return new EconomyResponse(v, 0, EconomyResponse.ResponseType.FAILURE, "Could not find player");
        }

        boolean allowNegativeBalance = FancyEconomy.getInstance().getFancyEconomyConfig().allowNegativeBalance();
        if (!allowNegativeBalance && currencyPlayer.getBalance() < v) {
            return new EconomyResponse(v, 0, EconomyResponse.ResponseType.FAILURE, "Not enough balance");
        }

        double maxNegativeBalance = FancyEconomy.getInstance().getFancyEconomyConfig().getMaxNegativeBalance();
        if (allowNegativeBalance && currencyPlayer.getBalance() - maxNegativeBalance < v) {
            return new EconomyResponse(v, 0, EconomyResponse.ResponseType.FAILURE, "Not enough balance");
        }

        currencyPlayer.removeBalance(currency, v);
        return new EconomyResponse(v, currencyPlayer.getBalance(currency), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(offlinePlayer.getUniqueId());
        if (currencyPlayer == null) {
            return new EconomyResponse(v, 0, EconomyResponse.ResponseType.FAILURE, "Could not find player");
        }

        boolean allowNegativeBalance = FancyEconomy.getInstance().getFancyEconomyConfig().allowNegativeBalance();
        if (!allowNegativeBalance && currencyPlayer.getBalance() < v) {
            return new EconomyResponse(v, 0, EconomyResponse.ResponseType.FAILURE, "Not enough balance");
        }

        double maxNegativeBalance = FancyEconomy.getInstance().getFancyEconomyConfig().getMaxNegativeBalance();
        if (allowNegativeBalance && currencyPlayer.getBalance() - maxNegativeBalance < v) {
            return new EconomyResponse(v, 0, EconomyResponse.ResponseType.FAILURE, "Not enough balance");
        }

        currencyPlayer.removeBalance(currency, v);
        return new EconomyResponse(v, currencyPlayer.getBalance(currency), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(s);
        if (currencyPlayer == null) {
            return new EconomyResponse(v, 0, EconomyResponse.ResponseType.FAILURE, "Could not find player");
        }

        boolean allowNegativeBalance = FancyEconomy.getInstance().getFancyEconomyConfig().allowNegativeBalance();
        if (!allowNegativeBalance && currencyPlayer.getBalance() < v) {
            return new EconomyResponse(v, 0, EconomyResponse.ResponseType.FAILURE, "Not enough balance");
        }

        double maxNegativeBalance = FancyEconomy.getInstance().getFancyEconomyConfig().getMaxNegativeBalance();
        if (allowNegativeBalance && currencyPlayer.getBalance() - maxNegativeBalance < v) {
            return new EconomyResponse(v, 0, EconomyResponse.ResponseType.FAILURE, "Not enough balance");
        }

        currencyPlayer.removeBalance(currency, v);
        return new EconomyResponse(v, currencyPlayer.getBalance(currency), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(offlinePlayer.getUniqueId());
        if (currencyPlayer == null) {
            return new EconomyResponse(v, 0, EconomyResponse.ResponseType.FAILURE, "Could not find player");
        }

        boolean allowNegativeBalance = FancyEconomy.getInstance().getFancyEconomyConfig().allowNegativeBalance();
        if (!allowNegativeBalance && currencyPlayer.getBalance() < v) {
            return new EconomyResponse(v, 0, EconomyResponse.ResponseType.FAILURE, "Not enough balance");
        }

        double maxNegativeBalance = FancyEconomy.getInstance().getFancyEconomyConfig().getMaxNegativeBalance();
        if (allowNegativeBalance && currencyPlayer.getBalance() - maxNegativeBalance < v) {
            return new EconomyResponse(v, 0, EconomyResponse.ResponseType.FAILURE, "Not enough balance");
        }

        currencyPlayer.removeBalance(currency, v);
        return new EconomyResponse(v, currencyPlayer.getBalance(currency), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(s);
        if (currencyPlayer == null) {
            return new EconomyResponse(v, 0, EconomyResponse.ResponseType.FAILURE, "Could not find player");
        }

        currencyPlayer.addBalance(currency, v);
        return new EconomyResponse(v, currencyPlayer.getBalance(currency), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(offlinePlayer.getUniqueId());
        if (currencyPlayer == null) {
            return new EconomyResponse(v, 0, EconomyResponse.ResponseType.FAILURE, "Could not find player");
        }

        currencyPlayer.addBalance(currency, v);
        return new EconomyResponse(v, currencyPlayer.getBalance(currency), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(s);
        if (currencyPlayer == null) {
            return new EconomyResponse(v, 0, EconomyResponse.ResponseType.FAILURE, "Could not find player");
        }

        currencyPlayer.addBalance(currency, v);
        return new EconomyResponse(v, currencyPlayer.getBalance(currency), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(offlinePlayer.getUniqueId());
        if (currencyPlayer == null) {
            return new EconomyResponse(v, 0, EconomyResponse.ResponseType.FAILURE, "Could not find player");
        }

        currencyPlayer.addBalance(currency, v);
        return new EconomyResponse(v, currencyPlayer.getBalance(currency), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<>();
    }

    @Override
    public boolean createPlayerAccount(String s) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(s);
        return currencyPlayer != null;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(offlinePlayer.getUniqueId());
        return currencyPlayer != null;
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(s);
        return currencyPlayer != null;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(offlinePlayer.getUniqueId());
        return currencyPlayer != null;
    }
}
