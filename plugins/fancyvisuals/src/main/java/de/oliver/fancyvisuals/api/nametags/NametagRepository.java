package de.oliver.fancyvisuals.api.nametags;

import de.oliver.fancyvisuals.api.Context;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The {@code NametagRepository} interface provides methods for retrieving {@code NametagStore} and {@code Nametag}
 * instances based on different contexts.
 */
public interface NametagRepository {

    /**
     * The default {@code Nametag} instance used when no specific nametag is found for a given context or player.
     */
    Nametag DEFAULT_NAMETAG = new Nametag(
            List.of("<gradient:#8c0010:#803c12>%player_name%</gradient>"),
            "#000000",
            true,
            Nametag.TextAlignment.CENTER
    );

    /**
     * Retrieves the {@code NametagStore} associated with the given context.
     *
     * @param context the context for which the store is to be retrieved. This determines if the store is for SERVER, WORLD, GROUP, or PLAYER.
     * @return the NametagStore associated with the provided context.
     */
    @NotNull NametagStore getStore(@NotNull Context context);

    /**
     * Retrieves the {@code Nametag} associated with the specified {@code id} within the given {@code context}.
     *
     * @param context the context for which the nametag is to be retrieved. This determines if the nametag is for SERVER, WORLD, GROUP, or PLAYER.
     * @param id      the unique identifier for the nametag.
     * @return the Nametag associated with the given id; may return null if no such nametag is found within the specified context.
     */
    default @Nullable Nametag getNametag(@NotNull Context context, @NotNull String id) {
        return getStore(context).getNametag(id);
    }

    /**
     * Retrieves the appropriate {@code Nametag} for the specified {@code Player} based on various contexts.
     * The method checks the PLAYER, GROUP, WORLD, and SERVER contexts in order, returning the first matching nametag found.
     * If no matching nametag is found in any context, a default nametag is returned.
     *
     * @param player the Player for whom the nametag is being retrieved
     * @return the Nametag associated with the player, or a default nametag if no specific nametag is found
     */
    @NotNull Nametag getNametagForPlayer(@NotNull Player player);

}
