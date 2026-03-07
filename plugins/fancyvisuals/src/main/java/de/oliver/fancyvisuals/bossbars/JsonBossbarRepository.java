package de.oliver.fancyvisuals.bossbars;

import de.oliver.fancyvisuals.api.Context;
import de.oliver.fancyvisuals.utils.ContextLookup;
import de.oliver.fancyvisuals.utils.JsonContextStore;
import de.oliver.jdb.JDB;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JsonBossbarRepository implements BossbarRepository {

    private static final String BASE_PATH = "plugins/FancyVisuals/data/bossbars/";

    private final Map<Context, JsonContextStore<BossbarSet>> stores;

    public JsonBossbarRepository() {
        stores = new ConcurrentHashMap<>();
        JDB jdb = new JDB(BASE_PATH);

        for (Context ctx : Context.values()) {
            stores.put(ctx, new JsonContextStore<>(jdb, ctx, BossbarSet.class));
        }

        initialConfig();
    }

    @Override
    public @NotNull BossbarSet getBossbarsForPlayer(@NotNull Player player) {
        return ContextLookup.resolve(player, (ctx, id) -> stores.get(ctx).get(id), DEFAULT_BOSSBARS);
    }

    private void initialConfig() {
        File baseDir = new File(BASE_PATH);
        if (baseDir.exists()) {
            return;
        }

        stores.get(Context.SERVER).set("global", DEFAULT_BOSSBARS);
    }
}
