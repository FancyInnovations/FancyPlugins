package de.oliver.fancylib.logging;

import de.oliver.fancyanalytics.logger.LogEntry;
import de.oliver.fancyanalytics.logger.middleware.Middleware;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginMiddleware implements Middleware {

    private static final Logger log = LoggerFactory.getLogger(PluginMiddleware.class);
    private final Plugin plugin;

    public PluginMiddleware(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable LogEntry process(LogEntry logEntry) {
        if (plugin == null) {
            return logEntry;
        }

        // server info
        logEntry.addProperty("server_software", plugin.getServer().getName());
        logEntry.addProperty("server_version", plugin.getServer().getBukkitVersion());
        logEntry.addProperty("is_online_mode", plugin.getServer().getOnlineMode());

        // plugin info
        logEntry.addProperty("plugin_name", plugin.getDescription().getName());
        logEntry.addProperty("plugin_version", plugin.getDescription().getVersion());

        return logEntry;
    }

}
