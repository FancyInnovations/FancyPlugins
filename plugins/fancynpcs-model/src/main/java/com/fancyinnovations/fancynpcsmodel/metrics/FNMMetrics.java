package com.fancyinnovations.fancynpcsmodel.metrics;

import com.fancyinnovations.fancynpcsmodel.main.FancyNpcsModelPlugin;
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

public class FNMMetrics {

    private final ExtendedFancyLogger logger;
    private FancyAnalyticsAPI fancyAnalytics;

    public FNMMetrics() {
        logger = FancyNpcsModelPlugin.get().getFancyLogger();
    }

    public void register() {
        fancyAnalytics = new FancyAnalyticsAPI("ddb636b6-25b5-4d44-abfc-3bb0dabd2b97", "-J4fYDk1Y2RhZDI3ZWRmZDRkYmT0usLA");
        fancyAnalytics.getConfig().setDisableLogging(true);

        fancyAnalytics.registerMinecraftPluginMetrics(FancyNpcsModelPlugin.get());
        fancyAnalytics.getExceptionHandler().registerLogger(FancyNpcsModelPlugin.get().getLogger());
        fancyAnalytics.getExceptionHandler().registerLogger(Bukkit.getLogger());
        fancyAnalytics.getExceptionHandler().registerLogger(logger);

        fancyAnalytics.registerStringMetric(new MetricSupplier<>("commit_hash", () -> FancyNpcsModelPlugin.get().getVersionConfig().getCommitHash().substring(0, 7)));

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

        fancyAnalytics.initialize();
    }

    public void checkIfPluginVersionUpdated() {
        VersionConfig versionConfig = FancyNpcsModelPlugin.get().getVersionConfig();
        String currentVersion = versionConfig.getVersion();
        String lastVersion = "N/A";

        File versionFile = new File(FancyNpcsModelPlugin.get().getDataFolder(), "version.yml");
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
