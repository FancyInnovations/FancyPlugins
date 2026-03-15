package de.oliver.fancyvisuals.bossbars;

import de.oliver.fancyvisuals.FancyVisuals;
import de.oliver.fancyvisuals.utils.TextRenderer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BossbarManager {

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    private final FancyVisuals plugin;
    private final BossbarRepository repository;
    private final ScheduledExecutorService scheduler;
    private final Map<UUID, Map<String, BossBar>> playerBars;

    public BossbarManager(FancyVisuals plugin, BossbarRepository repository) {
        this.plugin = plugin;
        this.repository = repository;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "BossbarScheduler"));
        this.playerBars = new ConcurrentHashMap<>();
    }

    public void init() {
        int intervalMs = plugin.getFancyVisualsConfig().getBossbarsUpdateIntervalMs();
        scheduler.scheduleWithFixedDelay(this::queueUpdateCycle, intervalMs, intervalMs, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        scheduler.shutdownNow();
    }

    public void clearAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            handleQuit(player);
        }
        playerBars.clear();
    }

    public void handleJoin(Player player) {
        if (!plugin.getFancyVisualsConfig().isBossbarsEnabled()) {
            return;
        }

        updateForPlayer(player);
    }

    public void handleQuit(Player player) {
        Map<String, BossBar> bars = playerBars.remove(player.getUniqueId());
        if (bars != null) {
            for (BossBar bar : bars.values()) {
                bar.removePlayer(player);
            }
        }
    }

    public void handleContextChange(Player player) {
        if (!plugin.getFancyVisualsConfig().isBossbarsEnabled()) {
            return;
        }

        updateForPlayer(player);
    }

    private void queueUpdateCycle() {
        Bukkit.getScheduler().runTask(plugin, this::runUpdateCycle);
    }

    private void runUpdateCycle() {
        if (!plugin.getFancyVisualsConfig().isBossbarsEnabled()) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            updateForPlayer(player);
        }
    }

    private void updateForPlayer(Player player) {
        BossbarSet set = repository.getBossbarsForPlayer(player);
        Map<String, BossBar> bars = playerBars.computeIfAbsent(player.getUniqueId(), key -> new HashMap<>());
        Set<String> seen = new HashSet<>();

        for (BossbarConfig config : set.bars()) {
            if (config == null || config.id().isBlank()) {
                continue;
            }

            seen.add(config.id());

            BossBar bar = bars.get(config.id());
            if (bar == null) {
                Set<BarFlag> flags = BossbarUtils.parseFlags(config.flags());
                bar = Bukkit.createBossBar("", BossbarUtils.parseColor(config.color()), BossbarUtils.parseStyle(config.style()), flags.toArray(new BarFlag[0]));
                bars.put(config.id(), bar);
            }

            Component title = TextRenderer.render(config.text(), player);
            bar.setTitle(LEGACY_SERIALIZER.serialize(title));
            bar.setProgress(clampProgress(config.progress()));
            bar.setColor(BossbarUtils.parseColor(config.color()));
            bar.setStyle(BossbarUtils.parseStyle(config.style()));

            Set<BarFlag> flags = BossbarUtils.parseFlags(config.flags());
            for (BarFlag flag : BarFlag.values()) {
                if (flags.contains(flag)) {
                    bar.addFlag(flag);
                } else {
                    bar.removeFlag(flag);
                }
            }

            bar.setVisible(config.visible());
            if (!bar.getPlayers().contains(player)) {
                bar.addPlayer(player);
            }
        }

        Set<String> toRemove = new HashSet<>(bars.keySet());
        toRemove.removeAll(seen);
        for (String id : toRemove) {
            BossBar bar = bars.remove(id);
            if (bar != null) {
                bar.removePlayer(player);
            }
        }
    }

    private double clampProgress(double progress) {
        if (progress < 0) {
            return 0;
        }
        if (progress > 1) {
            return 1;
        }
        return progress;
    }
}
