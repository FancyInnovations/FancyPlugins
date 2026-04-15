package de.oliver.fancyeconomy.currencies;

import java.util.*;

public class BalanceTop {
    private static final Map<Currency, BalanceTop> balanceTops = new HashMap<>();

    private final Currency currency;
    private Map<Integer, UUID> baltopPlaces;
    private Map<UUID, Integer> baltopPlayers;

    public BalanceTop(Currency currency) {
        this.currency = currency;
        this.baltopPlaces = new HashMap<>();
        this.baltopPlayers = new HashMap<>();
    }

    public static BalanceTop getForCurrency(Currency currency) {
        if (balanceTops.containsKey(currency)) {
            return balanceTops.get(currency);
        }

        BalanceTop baltop = new BalanceTop(currency);
        baltop.refreshBaltop();
        balanceTops.put(currency, baltop);

        return baltop;
    }

    public static void refreshAll() {
        balanceTops.values().forEach(BalanceTop::refreshBaltop);
    }

    public void refreshBaltop() {
        baltopPlaces.clear();
        baltopPlayers.clear();

        Collection<CurrencyPlayer> players = CurrencyPlayerManager.getAllPlayers();
        List<CurrencyPlayer> sorted = players.stream()
                .sorted(Comparator.comparing(cp -> cp.getBalance(currency)))
                .toList();
        ArrayList<CurrencyPlayer> sortedArrayList = new ArrayList<>(sorted);

        Collections.reverse(sortedArrayList);

        for (int i = 0; i < sortedArrayList.size(); i++) {
            int place = i + 1;
            UUID player = sortedArrayList.get(i).getUuid();
            baltopPlayers.put(player, place);
            baltopPlaces.put(place, player);
        }
    }

    public UUID getAtPlace(int place) {
        return baltopPlaces.getOrDefault(place, null);
    }

    public int getPlayerPlace(UUID player) {
        return baltopPlayers.getOrDefault(player, -1);
    }

    public int getAmountEntries() {
        return baltopPlaces.size();
    }

    public Currency getCurrency() {
        return currency;
    }
}
