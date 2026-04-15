package de.oliver.fancyeconomy.currencies;

import de.oliver.fancyeconomy.FancyEconomy;
import de.oliver.fancylib.databases.Database;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CurrencyPlayer {

    private final UUID uuid;
    private final Map<Currency, Double> balances;
    private String username;
    private boolean isDirty;

    public CurrencyPlayer(UUID uuid, String username, Map<Currency, Double> balances) {
        this.uuid = uuid;
        this.username = username;
        this.balances = balances;
        this.isDirty = false;
    }

    public CurrencyPlayer(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        this.balances = new HashMap<>();
        this.isDirty = false;
    }

    /**
     * @return the balance of the default currency
     */
    public double getBalance() {
        return balances.getOrDefault(CurrencyRegistry.getDefaultCurrency(), 0d);
    }

    public double getBalance(Currency currency) {
        return balances.getOrDefault(currency, 0d);
    }

    public void setBalance(Currency currency, double amount) {
        balances.put(currency, amount);
        isDirty = true;
    }

    public void addBalance(Currency currency, double amount) {
        setBalance(currency, getBalance(currency) + amount);
    }

    public void removeBalance(Currency currency, double amount) {
        setBalance(currency, getBalance(currency) - amount);
    }

    public void save(boolean force) {
        if (!(isDirty || force)) {
            return;
        }

        Database db = FancyEconomy.getInstance().getDatabase();

        db.executeNonQuery("REPLACE INTO players(uuid, username) VALUES('{uuid}', '{username}')"
                .replace("{uuid}", uuid.toString())
                .replace("{username}", username)
        );

        for (Currency currency : balances.keySet()) {
            double balance = balances.get(currency);

            db.executeNonQuery("REPLACE INTO balances(uuid, currency, balance) VALUES('{uuid}', '{currency}', {balance})"
                    .replace("{uuid}", uuid.toString())
                    .replace("{currency}", currency.name())
                    .replace("{balance}", String.valueOf(balance))
            );
        }

        isDirty = false;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username != null && !this.username.equals(username)) {
            this.username = username;
            this.isDirty = true;
        }
    }

    public Map<Currency, Double> getBalances() {
        return balances;
    }
}
