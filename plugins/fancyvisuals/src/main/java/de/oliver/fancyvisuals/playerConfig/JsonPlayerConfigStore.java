package de.oliver.fancyvisuals.playerConfig;

import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.fancyvisuals.FancyVisuals;
import de.oliver.jdb.JDB;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * The {@code JsonPlayerConfigStore} class is responsible for handling player configuration storage and retrieval using JSON.
 * It interacts with an underlying JSON database to manage {@code PlayerConfig} instances for individual players.
 */
public class JsonPlayerConfigStore {

    private static final String BASE_PATH = "plugins/FancyVisuals/data/player-configs/";
    private static final PlayerConfig DEFAULT_PLAYER_CONFIG = new PlayerConfig(true);
    private final JDB jdb;

    public JsonPlayerConfigStore() {
        jdb = new JDB(BASE_PATH);

        // Generate default player config if not present
        getDefaultPlayerConfig();
    }


    /**
     * Retrieves the PlayerConfig for a specific player identified by their UUID.
     * If the PlayerConfig is not found, the default PlayerConfig is returned.
     *
     * @param uuid the unique identifier of the player whose configuration is being retrieved.
     * @return the PlayerConfig associated with the given UUID, or the default PlayerConfig if none is found.
     */
    public @NotNull PlayerConfig getPlayerConfig(@NotNull UUID uuid) {
        PlayerConfig playerConfig = null;

        try {
            playerConfig = jdb.get(uuid.toString(), PlayerConfig.class);
        } catch (Exception e) {
            FancyVisuals.getFancyLogger().error("Failed to get player config for uuid " + uuid, ThrowableProperty.of(e));
        }

        return playerConfig != null ? playerConfig : getDefaultPlayerConfig();
    }

    /**
     * Sets the configuration for a specific player identified by the UUID.
     *
     * @param uuid         the unique identifier of the player for whom the configuration is to be set
     * @param playerConfig the PlayerConfig object containing the new configuration settings for the player
     */
    public void setPlayerConfig(@NotNull UUID uuid, @NotNull PlayerConfig playerConfig) {
        try {
            jdb.set(uuid.toString(), playerConfig);
        } catch (Exception e) {
            FancyVisuals.getFancyLogger().error("Failed to set player config for uuid " + uuid, ThrowableProperty.of(e));
        }
    }

    /**
     * Deletes the player configuration associated with the specified UUID.
     *
     * @param uuid the unique identifier of the player whose configuration is to be deleted
     */
    public void deletePlayerConfig(@NotNull UUID uuid) {
        jdb.delete(uuid.toString());
    }

    /**
     * Retrieves the default PlayerConfig.
     * If the default PlayerConfig is not found in the database, it sets and returns the predefined default configuration.
     *
     * @return the default PlayerConfig. If not present, the predefined default PlayerConfig is set and returned.
     */
    public PlayerConfig getDefaultPlayerConfig() {
        PlayerConfig playerConfig = null;

        try {
            playerConfig = jdb.get("default", PlayerConfig.class);
        } catch (Exception e) {
            FancyVisuals.getFancyLogger().error("Failed to get default player config", ThrowableProperty.of(e));
        }

        if (playerConfig == null) {
            playerConfig = DEFAULT_PLAYER_CONFIG;

            try {
                jdb.set("default", DEFAULT_PLAYER_CONFIG);
            } catch (Exception e) {
                FancyVisuals.getFancyLogger().error("Failed to set default player config", ThrowableProperty.of(e));
            }
        }

        return playerConfig;
    }
}
