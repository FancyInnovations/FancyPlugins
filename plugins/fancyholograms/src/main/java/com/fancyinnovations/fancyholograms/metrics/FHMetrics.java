package com.fancyinnovations.fancyholograms.metrics;

import com.fancyinnovations.fancyholograms.api.HologramRegistry;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancyanalytics.api.FancyAnalyticsAPI;
import de.oliver.fancyanalytics.api.metrics.MetricSupplier;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.sdk.events.Event;
import de.oliver.fancylib.Metrics;
import de.oliver.fancylib.VersionConfig;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class FHMetrics {

    private final ExtendedFancyLogger logger;
    private FancyAnalyticsAPI fancyAnalytics;

    public FHMetrics() {
        logger = FancyHologramsPlugin.get().getFancyLogger();
    }

    public void register() {
        fancyAnalytics = new FancyAnalyticsAPI("3b77bd59-2b01-46f2-b3aa-a9584401797f", "E2gW5zc2ZTk1OGFkNGY2ZDQ0ODlM6San");
        fancyAnalytics.getConfig().setDisableLogging(true);

        fancyAnalytics.registerMinecraftPluginMetrics(FancyHologramsPlugin.get());
        fancyAnalytics.getExceptionHandler().registerLogger(FancyHologramsPlugin.get().getLogger());
        fancyAnalytics.getExceptionHandler().registerLogger(Bukkit.getLogger());
        fancyAnalytics.getExceptionHandler().registerLogger(logger);

        HologramRegistry registry = FancyHologramsPlugin.get().getRegistry();

        fancyAnalytics.registerStringMetric(new MetricSupplier<>("commit_hash", () -> FancyHologramsPlugin.get().getVersionConfig().getCommitHash().substring(0, 7)));

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

        fancyAnalytics.registerNumberMetric(new MetricSupplier<>("amount_holograms", () -> (double) registry.getAll().size()));
        fancyAnalytics.registerStringMetric(new MetricSupplier<>("enabled_update_notifications", () -> Boolean.toString(!FancyHologramsPlugin.get().getFHConfiguration().areVersionNotificationsMuted())));
        fancyAnalytics.registerStringMetric(new MetricSupplier<>("fflag_disable_holograms_for_bedrock_players", () -> Boolean.toString(!FancyHologramsPlugin.get().getFHConfiguration().isHologramsForBedrockPlayersEnabled())));
        fancyAnalytics.registerStringMetric(new MetricSupplier<>("using_development_build", () -> Boolean.toString(FancyHologramsPlugin.get().getVersionConfig().isDevelopmentBuild())));

        fancyAnalytics.registerStringArrayMetric(new MetricSupplier<>("hologram_type", () -> {
            if (registry == null) {
                return new String[0];
            }

            return registry.getAll().stream()
                    .map(h -> h.getData().getType().name())
                    .toArray(String[]::new);
        }));

        fancyAnalytics.registerNumberMetric(new MetricSupplier<>("total_amount_attached_traits", () -> {
            double total = 0d;
            for (Hologram hologram : registry.getAll()) {
                total += hologram.getData().getTraitTrait().getTraits().size();
            }
            return total;
        }));


        fancyAnalytics.initialize();
    }

    public void registerLegacy() {
        Metrics metrics = new Metrics(FancyHologramsPlugin.get(), 17990);
        metrics.addCustomChart(new Metrics.SingleLineChart("total_holograms", () -> FancyHologramsPlugin.get().getRegistry().getAll().size()));
        metrics.addCustomChart(new Metrics.SimplePie("update_notifications", () -> FancyHologramsPlugin.get().getFHConfiguration().areVersionNotificationsMuted() ? "No" : "Yes"));
        metrics.addCustomChart(new Metrics.SimplePie("using_development_build", () -> FancyHologramsPlugin.get().getVersionConfig().isDevelopmentBuild() ? "Yes" : "No"));
    }

    public void checkIfPluginVersionUpdated() {
        VersionConfig versionConfig = FancyHologramsPlugin.get().getVersionConfig();
        String currentVersion = versionConfig.getVersion();
        String lastVersion = "N/A";

        File versionFile = new File(FancyHologramsPlugin.get().getDataFolder(), "version.yml");
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
