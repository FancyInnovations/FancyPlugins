package de.oliver.fancyvisuals.playerConfig;

import com.google.gson.annotations.SerializedName;

/**
 * Represents the configuration settings for a player.
 *
 * @param showOwnNametag indicates whether the player should see their own nametag.
 */
public record PlayerConfig(
        @SerializedName("show_own_nametag")
        boolean showOwnNametag
) {
}
