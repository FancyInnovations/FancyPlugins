package com.fancyinnovations.fancyworlds.metrics;

import com.fancyinnovations.fancyworlds.api.worlds.WorldService;
import com.fancyinnovations.fancyworlds.main.FancyWorldsPlugin;
import de.oliver.fancyanalytics.api.FancyAnalyticsAPI;
import de.oliver.fancyanalytics.api.metrics.MetricSupplier;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.sdk.events.Event;
import de.oliver.fancylib.VersionConfig;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class FWMetrics {

    private final ExtendedFancyLogger logger;
    private FancyAnalyticsAPI fancyAnalytics;

    public FWMetrics() {
        logger = FancyWorldsPlugin.get().getFancyLogger();
    }

    public void register() {
        fancyAnalytics = new FancyAnalyticsAPI("7f3344f8-7a39-49a5-917a-0cab45fcbb2a", "Wq3IR2E4YTM0MzA0YWMyODRhMDg_SvN-");
        fancyAnalytics.getConfig().setDisableLogging(true);

        fancyAnalytics.registerMinecraftPluginMetrics(FancyWorldsPlugin.get());
        fancyAnalytics.getExceptionHandler().registerLogger(FancyWorldsPlugin.get().getLogger());
        fancyAnalytics.getExceptionHandler().registerLogger(Bukkit.getLogger());
        fancyAnalytics.getExceptionHandler().registerLogger(logger);

        WorldService worldService = WorldService.get();

        fancyAnalytics.registerStringMetric(new MetricSupplier<>("commit_hash", () -> FancyWorldsPlugin.get().getVersionConfig().getCommitHash().substring(0, 7)));

        fancyAnalytics.registerStringMetric(new MetricSupplier<>("server_size", () -> {
            long onlinePlayers = Bukkit.getOnlinePlayers().size();

            if (onlinePlayers == 0) {
                return "empty";
            }

            if (onlinePlayers <= 25) {
                return "small";
            }

            if (onlinePlayers <= 100) {
                return "medium";
            }

            if (onlinePlayers <= 500) {
                return "large";
            }

            return "very_large";
        }));

        fancyAnalytics.registerNumberMetric(new MetricSupplier<>("amount_worlds", () -> (double) worldService.getAllWorlds().size()));
        fancyAnalytics.registerStringMetric(new MetricSupplier<>("enabled_update_notifications", () -> FancyWorldsPlugin.get().getFancyWorldsConfig().areVersionNotificationsMuted() ? "false" : "true"));
        fancyAnalytics.registerStringMetric(new MetricSupplier<>("using_development_build", () -> FancyWorldsPlugin.get().getVersionConfig().isDevelopmentBuild() ? "true" : "false"));

        fancyAnalytics.initialize();
    }

    public void checkIfPluginVersionUpdated() {
        VersionConfig versionConfig = FancyWorldsPlugin.get().getVersionConfig();
        String currentVersion = versionConfig.getVersion();
        String lastVersion = "N/A";

        File versionFile = new File(FancyWorldsPlugin.get().getDataFolder(), "version.yml");
        if (!versionFile.exists()) {
            try {
                Files.write(versionFile.toPath(), currentVersion.getBytes());
            } catch (IOException e) {
                logger.warn("Could not write version file.");
                return;
            }
        } else {
            try {
                lastVersion = new String(Files.readAllBytes(versionFile.toPath()));
            } catch (IOException e) {
                logger.warn("Could not read version file.");
                return;
            }
        }

        if (!lastVersion.equals(currentVersion)) {
            logger.info("Plugin has been updated from version " + lastVersion + " to " + currentVersion + ".");
            fancyAnalytics.sendEvent(
                    new Event("PluginVersionUpdated", new HashMap<>())
                            .withProperty("from", lastVersion)
                            .withProperty("to", currentVersion)
                            .withProperty("commit_hash", versionConfig.getCommitHash())
                            .withProperty("channel", versionConfig.getChannel())
                            .withProperty("platform", versionConfig.getPlatform())
            );

            try {
                Files.write(versionFile.toPath(), currentVersion.getBytes());
            } catch (IOException e) {
                logger.warn("Could not write version file.");
            }
        }
    }

    public FancyAnalyticsAPI getFancyAnalytics() {
        return fancyAnalytics;
    }

}
