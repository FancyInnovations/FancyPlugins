package de.oliver.fancyvisuals.actionbar;

import de.oliver.fancyvisuals.api.Context;
import de.oliver.fancyvisuals.utils.ContextLookup;
import de.oliver.fancyvisuals.utils.JsonContextStore;
import de.oliver.jdb.JDB;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JsonActionBarRepository implements ActionBarRepository {

    private static final String BASE_PATH = "plugins/FancyVisuals/data/actionbar/";

    private final Map<Context, JsonContextStore<ActionBarConfig>> stores;

    public JsonActionBarRepository() {
        stores = new ConcurrentHashMap<>();
        JDB jdb = new JDB(BASE_PATH);

        for (Context ctx : Context.values()) {
            stores.put(ctx, new JsonContextStore<>(jdb, ctx, ActionBarConfig.class));
        }

        initialConfig();
    }

    @Override
    public @NotNull ActionBarConfig getActionBarForPlayer(@NotNull Player player) {
        return ContextLookup.resolve(player, (ctx, id) -> stores.get(ctx).get(id), DEFAULT_ACTIONBAR);
    }

    private void initialConfig() {
        File baseDir = new File(BASE_PATH);
        if (baseDir.exists()) {
            return;
        }

        stores.get(Context.SERVER).set("global", DEFAULT_ACTIONBAR);
    }
}
