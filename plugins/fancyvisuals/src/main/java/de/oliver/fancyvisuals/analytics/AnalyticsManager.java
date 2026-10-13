package de.oliver.fancyvisuals.analytics;

import de.oliver.fancyanalytics.api.FancyAnalyticsAPI;
import de.oliver.fancyanalytics.api.metrics.MetricSupplier;
import de.oliver.fancyvisuals.FancyVisuals;
import de.oliver.fancyvisuals.api.Context;
import de.oliver.fancyvisuals.api.nametags.NametagRepository;

import java.util.logging.Logger;

public class AnalyticsManager {

    private final FancyAnalyticsAPI fa;

    public AnalyticsManager(String projectId, String apiKey) {
        this.fa = new FancyAnalyticsAPI(projectId, apiKey);
    }

    public void init() {
        fa.registerMinecraftPluginMetrics(FancyVisuals.get());
        fa.getExceptionHandler().registerLogger(FancyVisuals.get().getLogger());
        fa.getExceptionHandler().registerLogger(Logger.getGlobal());

        registerNametagMetrics();

        fa.initialize();
    }

    private void registerNametagMetrics() {
        NametagRepository repo = FancyVisuals.get().getNametagRepository();

        fa.registerNumberMetric(new MetricSupplier<>("nametag_count_total", () -> {
            double count = 0;
            for (Context ctx : Context.values()) {
                count += repo.getStore(ctx).getNametags().size();
            }
            return count;
        }));
        fa.registerNumberMetric(new MetricSupplier<>("nametag_count_player", () -> (double) repo.getStore(Context.PLAYER).getNametags().size()));
        fa.registerNumberMetric(new MetricSupplier<>("nametag_count_group", () -> (double) repo.getStore(Context.GROUP).getNametags().size()));
        fa.registerNumberMetric(new MetricSupplier<>("nametag_count_world", () -> (double) repo.getStore(Context.WORLD).getNametags().size()));
        fa.registerNumberMetric(new MetricSupplier<>("nametag_count_server", () -> (double) repo.getStore(Context.SERVER).getNametags().size()));
    }
}
