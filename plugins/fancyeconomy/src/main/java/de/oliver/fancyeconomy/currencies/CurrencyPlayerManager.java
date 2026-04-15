package de.oliver.fancyeconomy.currencies;

import de.oliver.fancyeconomy.FancyEconomy;
import de.oliver.fancylib.UUIDFetcher;
import de.oliver.fancylib.databases.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CurrencyPlayerManager {

    private static final Map<UUID, CurrencyPlayer> cachedPlayers = new HashMap<>();

    public static CurrencyPlayer getPlayer(UUID uuid) {
        if (cachedPlayers.containsKey(uuid)) {
            return cachedPlayers.get(uuid);
        }

        String username = UUIDFetcher.getName(uuid);

        CurrencyPlayer player = new CurrencyPlayer(uuid, username != null ? username : "N/A");
        cachedPlayers.put(uuid, player);
        FancyEconomy.getInstance().getSaveWorkload().addValue(() -> player);
        return player;
    }

    public static CurrencyPlayer getPlayer(String username) {
        for (CurrencyPlayer player : cachedPlayers.values()) {
            if (player.getUsername().equalsIgnoreCase(username)) {
                return player;
            }
        }

        UUID uuid = UUIDFetcher.getUUID(username);
        if (uuid == null) {
            return null;
        }

        CurrencyPlayer player = new CurrencyPlayer(uuid, username);
        cachedPlayers.put(uuid, player);
        FancyEconomy.getInstance().getSaveWorkload().addValue(() -> player);
        return player;
    }

    public static Collection<CurrencyPlayer> getAllPlayers() {
        return cachedPlayers.values();
    }

    public static String[] getAllPlayerNames() {
        return cachedPlayers.values().stream()
                .map(CurrencyPlayer::getUsername)
                .filter(s -> !s.equalsIgnoreCase("N/A"))
                .toArray(String[]::new);
    }

    public static void loadPlayersFromDatabase() {
        Database db = FancyEconomy.getInstance().getDatabase();

        cachedPlayers.clear();

        /*
            Load players
         */
        ResultSet rsPlayers = db.executeQuery("SELECT * FROM players");
        try {
            while (rsPlayers.next()) {
                String uuidStr = rsPlayers.getString("uuid");
                UUID uuid = UUID.fromString(uuidStr);
                if (uuid == null) {
                    continue;
                }

                String username = rsPlayers.getString("username");

                CurrencyPlayer currencyPlayer = new CurrencyPlayer(uuid, username);
                cachedPlayers.put(uuid, currencyPlayer);
                FancyEconomy.getInstance().getSaveWorkload().addValue(() -> currencyPlayer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*
            Load balances
         */
        ResultSet rsBalances = db.executeQuery("SELECT * FROM balances");
        try {
            while (rsBalances.next()) {
                String uuidStr = rsBalances.getString("uuid");
                UUID uuid = UUID.fromString(uuidStr);
                if (uuid == null) {
                    continue;
                }

                String currencyName = rsBalances.getString("currency");
                Currency currency = CurrencyRegistry.getCurrencyByName(currencyName);
                if (currency == null) {
                    continue;
                }

                double balance = rsBalances.getDouble("balance");

                if (cachedPlayers.containsKey(uuid)) {
                    CurrencyPlayer currencyPlayer = cachedPlayers.get(uuid);
                    currencyPlayer.getBalances().put(currency, balance);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
