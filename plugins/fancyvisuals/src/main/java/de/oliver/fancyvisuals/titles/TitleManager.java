package de.oliver.fancyvisuals.titles;

import de.oliver.fancyvisuals.FancyVisuals;
import de.oliver.fancyvisuals.utils.TextRenderer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TitleManager {

    private final FancyVisuals plugin;
    private final TitleRepository repository;
    private final ScheduledExecutorService scheduler;
    private final Map<UUID, TitleState> states;

    public TitleManager(FancyVisuals plugin, TitleRepository repository) {
        this.plugin = plugin;
        this.repository = repository;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "TitleScheduler"));
        this.states = new ConcurrentHashMap<>();
    }

    public void init() {
        int intervalMs = plugin.getFancyVisualsConfig().getTitlesUpdateIntervalMs();
        scheduler.scheduleWithFixedDelay(this::queueUpdateCycle, intervalMs, intervalMs, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        scheduler.shutdownNow();
    }

    public void clearAll() {
        states.clear();
    }

    public void handleJoin(Player player) {
        if (!plugin.getFancyVisualsConfig().isTitlesEnabled()) {
            return;
        }

        updateForPlayer(player);
    }

    public void handleQuit(Player player) {
        states.remove(player.getUniqueId());
    }

    public void handleContextChange(Player player) {
        if (!plugin.getFancyVisualsConfig().isTitlesEnabled()) {
            return;
        }

        updateForPlayer(player);
    }

    private void queueUpdateCycle() {
        Bukkit.getScheduler().runTask(plugin, this::runUpdateCycle);
    }

    private void runUpdateCycle() {
        if (!plugin.getFancyVisualsConfig().isTitlesEnabled()) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            updateForPlayer(player);
        }
    }

    private void updateForPlayer(Player player) {
        TitleConfig config = repository.getTitlesForPlayer(player);
        List<TitleMessage> messages = config.messages();
        if (messages.isEmpty()) {
            states.remove(player.getUniqueId());
            return;
        }

        int intervalMs = config.intervalMs() > 0 ? config.intervalMs() : plugin.getFancyVisualsConfig().getTitlesUpdateIntervalMs();
        TitleState state = states.computeIfAbsent(player.getUniqueId(), key -> new TitleState());

        long now = System.currentTimeMillis();
        if (now - state.lastSentAt < intervalMs) {
            return;
        }

        int index = state.index % messages.size();
        TitleMessage message = messages.get(index);

        Component title = TextRenderer.render(message.title(), player);
        Component subtitle = TextRenderer.render(message.subtitle(), player);

        Title.Times times = Title.Times.times(
                ticksToDuration(message.fadeInTicks()),
                ticksToDuration(message.stayTicks()),
                ticksToDuration(message.fadeOutTicks())
        );

        player.showTitle(Title.title(title, subtitle, times));

        state.index = (state.index + 1) % messages.size();
        state.lastSentAt = now;
    }

    private Duration ticksToDuration(int ticks) {
        return Duration.ofMillis(Math.max(0, ticks) * 50L);
    }

    private static class TitleState {
        private long lastSentAt;
        private int index;
    }
}
