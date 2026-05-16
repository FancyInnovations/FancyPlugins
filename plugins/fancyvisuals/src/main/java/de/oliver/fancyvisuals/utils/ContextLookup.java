package de.oliver.fancyvisuals.utils;

import de.oliver.fancyvisuals.api.Context;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public final class ContextLookup {

    private ContextLookup() {
    }

    public static <T> @Nullable T resolve(@NotNull Player player,
                                          @NotNull BiFunction<Context, String, @Nullable T> lookup,
                                          @Nullable T fallback) {
        T value = lookup.apply(Context.PLAYER, player.getUniqueId().toString());
        if (value != null) {
            return value;
        }

        if (VaultHelper.isVaultLoaded() && VaultHelper.getPermission() != null) {
            String group = VaultHelper.getPermission().getPrimaryGroup(player);
            if (group != null && !group.isEmpty()) {
                value = lookup.apply(Context.GROUP, group);
                if (value != null) {
                    return value;
                }
            }
        }

        value = lookup.apply(Context.WORLD, player.getWorld().getName());
        if (value != null) {
            return value;
        }

        value = lookup.apply(Context.SERVER, "global");
        if (value != null) {
            return value;
        }

        return fallback;
    }
}
