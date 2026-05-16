package de.oliver.fancyvisuals.tablist;

import de.oliver.fancyvisuals.api.Context;
import de.oliver.fancyvisuals.tablist.data.CustomTablistEntries;
import de.oliver.fancyvisuals.tablist.data.CustomTablistEntry;
import de.oliver.fancyvisuals.tablist.data.TablistHeaderFooter;
import de.oliver.fancyvisuals.tablist.data.TablistPlayerFormat;
import de.oliver.fancyvisuals.utils.ContextLookup;
import de.oliver.fancyvisuals.utils.JsonContextStore;
import de.oliver.jdb.JDB;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JsonTablistRepository implements TablistRepository {

    private static final String HEADER_FOOTER_BASE = "plugins/FancyVisuals/data/tablist/header-footer/";
    private static final String PLAYER_FORMAT_BASE = "plugins/FancyVisuals/data/tablist/player-format/";
    private static final String CUSTOM_ENTRIES_BASE = "plugins/FancyVisuals/data/tablist/custom-entries/";

    private final Map<Context, JsonContextStore<TablistHeaderFooter>> headerStores;
    private final Map<Context, JsonContextStore<TablistPlayerFormat>> formatStores;
    private final Map<Context, JsonContextStore<CustomTablistEntries>> customStores;

    public JsonTablistRepository() {
        headerStores = new ConcurrentHashMap<>();
        formatStores = new ConcurrentHashMap<>();
        customStores = new ConcurrentHashMap<>();

        JDB headerDb = new JDB(HEADER_FOOTER_BASE);
        JDB formatDb = new JDB(PLAYER_FORMAT_BASE);
        JDB customDb = new JDB(CUSTOM_ENTRIES_BASE);

        for (Context ctx : Context.values()) {
            headerStores.put(ctx, new JsonContextStore<>(headerDb, ctx, TablistHeaderFooter.class));
            formatStores.put(ctx, new JsonContextStore<>(formatDb, ctx, TablistPlayerFormat.class));
            customStores.put(ctx, new JsonContextStore<>(customDb, ctx, CustomTablistEntries.class));
        }

        initialConfig();
    }

    @Override
    public @NotNull TablistHeaderFooter getHeaderFooterForPlayer(@NotNull Player player) {
        return ContextLookup.resolve(player, (ctx, id) -> headerStores.get(ctx).get(id), DEFAULT_HEADER_FOOTER);
    }

    @Override
    public @NotNull TablistPlayerFormat getPlayerFormatForPlayer(@NotNull Player player) {
        return ContextLookup.resolve(player, (ctx, id) -> formatStores.get(ctx).get(id), DEFAULT_PLAYER_FORMAT);
    }

    @Override
    public @NotNull CustomTablistEntries getCustomEntriesForPlayer(@NotNull Player player) {
        return ContextLookup.resolve(player, (ctx, id) -> customStores.get(ctx).get(id), DEFAULT_CUSTOM_ENTRIES);
    }

    private void initialConfig() {
        File headerDir = new File(HEADER_FOOTER_BASE);
        File formatDir = new File(PLAYER_FORMAT_BASE);
        File customDir = new File(CUSTOM_ENTRIES_BASE);

        if (headerDir.exists() || formatDir.exists() || customDir.exists()) {
            return;
        }

        headerStores.get(Context.SERVER).set("global", DEFAULT_HEADER_FOOTER);

        formatStores.get(Context.SERVER).set("global", DEFAULT_PLAYER_FORMAT);
        formatStores.get(Context.GROUP).set("admin", new TablistPlayerFormat(
                "<red>[Admin] ",
                "%player_name%",
                "",
                10
        ));
        formatStores.get(Context.GROUP).set("moderator", new TablistPlayerFormat(
                "<gold>[Mod] ",
                "%player_name%",
                "",
                20
        ));

        customStores.get(Context.SERVER).set("global", new CustomTablistEntries(List.of(
                new CustomTablistEntry(
                        "website",
                        "<gray>website: <white>example.com</white>",
                        5,
                        0,
                        "SURVIVAL",
                        "fv_website",
                        null
                )
        )));
    }
}
