package de.oliver.fancyvisuals.actionbar;

import de.oliver.fancyvisuals.FancyVisuals;
import de.oliver.fancyvisuals.utils.TextRenderer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ActionBarManager {

    private final FancyVisuals plugin;
    private final ActionBarRepository repository;
    private final ScheduledExecutorService scheduler;
    private final Map<UUID, ActionBarState> states;

    public ActionBarManager(FancyVisuals plugin, ActionBarRepository repository) {
        this.plugin = plugin;
        this.repository = repository;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "ActionBarScheduler"));
        this.states = new ConcurrentHashMap<>();
    }

    public void init() {
        int intervalMs = plugin.getFancyVisualsConfig().getActionBarUpdateIntervalMs();
        scheduler.scheduleWithFixedDelay(this::queueUpdateCycle, intervalMs, intervalMs, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        scheduler.shutdownNow();
    }

    public void clearAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendActionBar(Component.empty());
        }
        states.clear();
    }

    public void handleJoin(Player player) {
        if (!plugin.getFancyVisualsConfig().isActionBarEnabled()) {
            return;
        }

        updateForPlayer(player);
    }

    public void handleQuit(Player player) {
        states.remove(player.getUniqueId());
    }

    public void handleContextChange(Player player) {
        if (!plugin.getFancyVisualsConfig().isActionBarEnabled()) {
            return;
        }

        updateForPlayer(player);
    }

    private void queueUpdateCycle() {
        Bukkit.getScheduler().runTask(plugin, this::runUpdateCycle);
    }

    private void runUpdateCycle() {
        if (!plugin.getFancyVisualsConfig().isActionBarEnabled()) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            updateForPlayer(player);
        }
    }

    private void updateForPlayer(Player player) {
        ActionBarConfig config = repository.getActionBarForPlayer(player);
        List<String> messages = config.messages();
        if (messages.isEmpty()) {
            player.sendActionBar(Component.empty());
            states.remove(player.getUniqueId());
            return;
        }

        int intervalMs = config.intervalMs() > 0 ? config.intervalMs() : plugin.getFancyVisualsConfig().getActionBarUpdateIntervalMs();
        ActionBarState state = states.computeIfAbsent(player.getUniqueId(), key -> new ActionBarState());

        long now = System.currentTimeMillis();
        if (now - state.lastSentAt < intervalMs) {
            return;
        }

        int index = state.index % messages.size();
        String message = messages.get(index);
        player.sendActionBar(TextRenderer.render(message, player));

        state.index = (state.index + 1) % messages.size();
        state.lastSentAt = now;
    }

    private static class ActionBarState {
        private long lastSentAt;
        private int index;
    }
}
