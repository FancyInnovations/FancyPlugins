package de.oliver.fancyvisuals.nametags.fake;

import de.oliver.fancyvisuals.api.Context;
import de.oliver.fancyvisuals.api.nametags.Nametag;
import de.oliver.fancyvisuals.api.nametags.NametagRepository;
import de.oliver.fancyvisuals.api.nametags.NametagStore;
import de.oliver.fancyvisuals.utils.VaultHelper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FakeNametagRepository implements NametagRepository {

    private final Map<Context, NametagStore> stores;

    public FakeNametagRepository() {
        this.stores = new ConcurrentHashMap<>();

        for (Context ctx : Context.values()) {
            stores.put(ctx, new FakeNametagStore());
        }
    }

    @Override
    public @NotNull NametagStore getStore(@NotNull Context context) {
        return stores.get(context);
    }

    @Override
    @NotNull
    public Nametag getNametagForPlayer(@NotNull Player player) {
        Nametag nametag = getNametag(Context.PLAYER, player.getUniqueId().toString());
        if (nametag != null) {
            return nametag;
        }

        if (VaultHelper.isVaultLoaded()) {
            nametag = getNametag(Context.GROUP, VaultHelper.getPermission().getPrimaryGroup(player));
            if (nametag != null) {
                return nametag;
            }
        }

        nametag = getNametag(Context.WORLD, player.getWorld().getName());
        if (nametag != null) {
            return nametag;
        }

        nametag = getNametag(Context.SERVER, "global");
        if (nametag != null) {
            return nametag;
        }

        return DEFAULT_NAMETAG;
    }
}
