package de.oliver.fancyvisuals.nametags;

import de.oliver.fancyvisuals.api.nametags.Nametag;
import de.oliver.fancyvisuals.api.nametags.NametagRepository;
import de.oliver.fancyvisuals.nametags.visibility.PlayerNametag;
import de.oliver.fancyvisuals.nametags.visibility.PlayerNametagScheduler;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NametagManager {

    private final NametagRepository repository;
    private final PlayerNametagScheduler scheduler;
    private final Map<UUID, PlayerNametag> activeNametags;

    public NametagManager(NametagRepository repository, PlayerNametagScheduler scheduler) {
        this.repository = repository;
        this.scheduler = scheduler;
        this.activeNametags = new ConcurrentHashMap<>();
    }

    public void handlePlayerUpdate(Player player) {
        Nametag nametag = repository.getNametagForPlayer(player);
        PlayerNametag existing = activeNametags.get(player.getUniqueId());

        if (existing == null) {
            PlayerNametag playerNametag = new PlayerNametag(nametag, player);
            activeNametags.put(player.getUniqueId(), playerNametag);
            scheduler.add(playerNametag);
        } else {
            existing.setNametag(nametag);
        }
    }

    public void handleQuit(Player player) {
        PlayerNametag existing = activeNametags.remove(player.getUniqueId());
        if (existing != null) {
            existing.hideFromAll();
        }
    }

    public void shutdown() {
        for (PlayerNametag nametag : activeNametags.values()) {
            nametag.hideFromAll();
        }
        activeNametags.clear();
    }
}
