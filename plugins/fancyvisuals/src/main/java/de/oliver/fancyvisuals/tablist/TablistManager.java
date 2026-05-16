package de.oliver.fancyvisuals.tablist;

import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.packets.FS_ClientboundPlayerInfoRemovePacket;
import de.oliver.fancysitula.api.packets.FS_ClientboundPlayerInfoUpdatePacket;
import de.oliver.fancysitula.api.utils.FS_GameProfile;
import de.oliver.fancysitula.api.utils.FS_GameType;
import de.oliver.fancysitula.factories.FancySitula;
import de.oliver.fancyvisuals.FancyVisuals;
import de.oliver.fancyvisuals.tablist.data.CustomTablistEntries;
import de.oliver.fancyvisuals.tablist.data.CustomTablistEntry;
import de.oliver.fancyvisuals.tablist.data.TablistHeaderFooter;
import de.oliver.fancyvisuals.tablist.data.TablistPlayerFormat;
import de.oliver.fancyvisuals.tablist.data.TablistSkin;
import de.oliver.fancyvisuals.utils.TextRenderer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TablistManager {

    private final FancyVisuals plugin;
    private final TablistRepository repository;
    private final TablistTeamManager teamManager;
    private final ScheduledExecutorService scheduler;

    private final Map<UUID, HeaderFooterState> headerFooterCache;
    private final Map<UUID, Component> playerEntryCache;
    private final Map<UUID, Integer> playerSortCache;
    private final Map<UUID, Map<String, CustomEntryState>> customEntryCache;

    public TablistManager(FancyVisuals plugin, TablistRepository repository) {
        this.plugin = plugin;
        this.repository = repository;
        this.teamManager = new TablistTeamManager();
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "TablistScheduler"));

        this.headerFooterCache = new ConcurrentHashMap<>();
        this.playerEntryCache = new ConcurrentHashMap<>();
        this.playerSortCache = new ConcurrentHashMap<>();
        this.customEntryCache = new ConcurrentHashMap<>();
    }

    public void init() {
        int intervalMs = plugin.getFancyVisualsConfig().getTablistUpdateIntervalMs();
        scheduler.scheduleWithFixedDelay(this::queueUpdateCycle, intervalMs, intervalMs, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        scheduler.shutdownNow();
    }

    public void clearAll() {
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            Map<String, CustomEntryState> states = customEntryCache.get(viewer.getUniqueId());
            if (states != null) {
                for (CustomEntryState state : states.values()) {
                    removeCustomEntry(viewer, state);
                }
            }
            teamManager.removeViewer(viewer);
        }
        customEntryCache.clear();
        headerFooterCache.clear();
        playerEntryCache.clear();
        playerSortCache.clear();
    }

    public void handleJoin(Player player) {
        if (!plugin.getFancyVisualsConfig().isTablistEnabled()) {
            return;
        }

        if (plugin.getFancyVisualsConfig().isTablistHeaderFooterEnabled()) {
            updateHeaderFooter(player);
        }

        if (plugin.getFancyVisualsConfig().isTablistCustomEntriesEnabled()) {
            updateCustomEntries(player);
        }

        if (plugin.getFancyVisualsConfig().isTablistEntriesEnabled()) {
            sendAllEntriesToViewer(player);
            updatePlayerEntry(player);
        } else if (shouldAssignTeams()) {
            assignTeamsForViewer(player);
        }
    }

    public void handleQuit(Player player) {
        headerFooterCache.remove(player.getUniqueId());
        customEntryCache.remove(player.getUniqueId());
        teamManager.removeViewer(player);
    }

    public void handleContextChange(Player player) {
        if (!plugin.getFancyVisualsConfig().isTablistEnabled()) {
            return;
        }

        if (plugin.getFancyVisualsConfig().isTablistHeaderFooterEnabled()) {
            updateHeaderFooter(player);
        }

        if (plugin.getFancyVisualsConfig().isTablistCustomEntriesEnabled()) {
            updateCustomEntries(player);
        }

        if (plugin.getFancyVisualsConfig().isTablistEntriesEnabled()) {
            updatePlayerEntry(player);
        } else if (shouldAssignTeams()) {
            updatePlayerTeams(player);
        }
    }

    private void queueUpdateCycle() {
        Bukkit.getScheduler().runTask(plugin, this::runUpdateCycle);
    }

    private void runUpdateCycle() {
        if (!plugin.getFancyVisualsConfig().isTablistEnabled()) {
            return;
        }

        if (plugin.getFancyVisualsConfig().isTablistHeaderFooterEnabled()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateHeaderFooter(player);
            }
        }

        if (plugin.getFancyVisualsConfig().isTablistCustomEntriesEnabled()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateCustomEntries(player);
            }
        }

        if (plugin.getFancyVisualsConfig().isTablistEntriesEnabled()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updatePlayerEntry(player);
            }
        } else if (shouldAssignTeams()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updatePlayerTeams(player);
            }
        }
    }

    private void updateHeaderFooter(Player player) {
        TablistHeaderFooter data = repository.getHeaderFooterForPlayer(player);
        Component header = TextRenderer.render(data.header(), player);
        Component footer = TextRenderer.render(data.footer(), player);

        HeaderFooterState cached = headerFooterCache.get(player.getUniqueId());
        if (cached != null && cached.header.equals(header) && cached.footer.equals(footer)) {
            return;
        }

        player.sendPlayerListHeaderAndFooter(header, footer);
        headerFooterCache.put(player.getUniqueId(), new HeaderFooterState(header, footer));
    }

    private void updatePlayerEntry(Player player) {
        TablistPlayerFormat format = repository.getPlayerFormatForPlayer(player);
        Component display = buildDisplayName(format, player);

        Component cachedDisplay = playerEntryCache.get(player.getUniqueId());
        if (cachedDisplay == null || !cachedDisplay.equals(display)) {
            sendDisplayUpdateToAllViewers(player, display);
            playerEntryCache.put(player.getUniqueId(), display);
        }

        int sortPriority = format.sortPriority();
        Integer cachedSort = playerSortCache.get(player.getUniqueId());
        if (cachedSort == null || cachedSort != sortPriority) {
            assignSortTeamToAllViewers(player, sortPriority);
            playerSortCache.put(player.getUniqueId(), sortPriority);
        }
    }

    private void sendAllEntriesToViewer(Player viewer) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            TablistPlayerFormat format = repository.getPlayerFormatForPlayer(target);
            Component display = playerEntryCache.computeIfAbsent(target.getUniqueId(), key -> buildDisplayName(format, target));
            sendDisplayUpdate(viewer, target, display);

            int sortPriority = format.sortPriority();
            playerSortCache.putIfAbsent(target.getUniqueId(), sortPriority);
            teamManager.assignEntity(viewer, target.getName(), sortPriority);
        }
    }

    private void assignTeamsForViewer(Player viewer) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            TablistPlayerFormat format = repository.getPlayerFormatForPlayer(target);
            int sortPriority = format.sortPriority();
            playerSortCache.putIfAbsent(target.getUniqueId(), sortPriority);
            teamManager.assignEntity(viewer, target.getName(), sortPriority);
        }
    }

    private void assignSortTeamToAllViewers(Player target, int sortPriority) {
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            teamManager.assignEntity(viewer, target.getName(), sortPriority);
        }
    }

    private void sendDisplayUpdateToAllViewers(Player target, Component display) {
        FS_ClientboundPlayerInfoUpdatePacket packet = buildPlayerInfoUpdate(target, display);
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            packet.send(new FS_RealPlayer(viewer));
        }
    }

    private void sendDisplayUpdate(Player viewer, Player target, Component display) {
        FS_ClientboundPlayerInfoUpdatePacket packet = buildPlayerInfoUpdate(target, display);
        packet.send(new FS_RealPlayer(viewer));
    }

    private void updatePlayerTeams(Player player) {
        TablistPlayerFormat format = repository.getPlayerFormatForPlayer(player);
        int sortPriority = format.sortPriority();
        Integer cachedSort = playerSortCache.get(player.getUniqueId());
        if (cachedSort == null || cachedSort != sortPriority) {
            assignSortTeamToAllViewers(player, sortPriority);
            playerSortCache.put(player.getUniqueId(), sortPriority);
        }
    }

    private FS_ClientboundPlayerInfoUpdatePacket buildPlayerInfoUpdate(Player target, Component display) {
        FS_GameProfile profile = TablistUtils.toProfile(target);
        FS_GameType gameMode = TablistUtils.toGameType(target.getGameMode());
        int ping = Math.max(0, target.getPing());

        FS_ClientboundPlayerInfoUpdatePacket.Entry entry = new FS_ClientboundPlayerInfoUpdatePacket.Entry(
                target.getUniqueId(),
                profile,
                true,
                ping,
                gameMode,
                display
        );

        return FancySitula.PACKET_FACTORY.createPlayerInfoUpdatePacket(
                EnumSet.of(FS_ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME),
                List.of(entry)
        );
    }

    private Component buildDisplayName(TablistPlayerFormat format, Player player) {
        String prefix = format.prefix() == null ? "" : format.prefix();
        String name = (format.name() == null || format.name().isBlank()) ? "%player_name%" : format.name();
        String suffix = format.suffix() == null ? "" : format.suffix();
        String raw = prefix + name + suffix;
        return TextRenderer.render(raw, player);
    }

    private boolean shouldAssignTeams() {
        return plugin.getNametagConfig().hideVanillaNametag();
    }

    private void updateCustomEntries(Player viewer) {
        CustomTablistEntries entries = repository.getCustomEntriesForPlayer(viewer);
        Map<String, CustomEntryState> states = customEntryCache.computeIfAbsent(viewer.getUniqueId(), key -> new HashMap<>());
        Set<String> seen = new HashSet<>();

        for (CustomTablistEntry entry : entries.entries()) {
            if (entry == null || entry.id().isBlank()) {
                continue;
            }

            seen.add(entry.id());

            String profileName = TablistUtils.buildProfileName(entry);
            UUID entryUuid = TablistUtils.buildEntryUuid(viewer, entry);
            Component display = TextRenderer.render(entry.text(), viewer);

            CustomEntryState state = states.get(entry.id());
            if (state == null || !state.profileName.equals(profileName) || !skinsMatch(state, entry)) {
                if (state != null) {
                    removeCustomEntry(viewer, state);
                }

                addCustomEntry(viewer, entry, entryUuid, profileName, display);
                states.put(entry.id(), new CustomEntryState(entryUuid, profileName, display, entry.sortPriority(), entry.skin()));
            } else {
                if (!state.display.equals(display)) {
                    updateCustomEntryDisplay(viewer, entryUuid, profileName, entry, display);
                    state.display = display;
                }

                if (state.sortPriority != entry.sortPriority()) {
                    state.sortPriority = entry.sortPriority();
                    teamManager.assignEntity(viewer, profileName, entry.sortPriority());
                } else {
                    teamManager.assignEntity(viewer, profileName, entry.sortPriority());
                }
            }
        }

        Set<String> toRemove = new HashSet<>(states.keySet());
        toRemove.removeAll(seen);
        for (String entryId : toRemove) {
            CustomEntryState state = states.remove(entryId);
            if (state != null) {
                removeCustomEntry(viewer, state);
            }
        }
    }

    private void addCustomEntry(Player viewer, CustomTablistEntry entry, UUID uuid, String profileName, Component display) {
        FS_GameProfile profile = new FS_GameProfile(uuid, profileName);
        if (entry.skin() != null) {
            entry.skin().applyTo(profile);
        }

        FS_GameType gameMode = TablistUtils.toGameType(entry.gameMode());
        int ping = Math.max(0, entry.ping());

        FS_ClientboundPlayerInfoUpdatePacket.Entry packetEntry = new FS_ClientboundPlayerInfoUpdatePacket.Entry(
                uuid,
                profile,
                true,
                ping,
                gameMode,
                display
        );

        FS_ClientboundPlayerInfoUpdatePacket packet = FancySitula.PACKET_FACTORY.createPlayerInfoUpdatePacket(
                EnumSet.of(
                        FS_ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
                        FS_ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED,
                        FS_ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME
                ),
                List.of(packetEntry)
        );

        packet.send(new FS_RealPlayer(viewer));
        teamManager.assignEntity(viewer, profileName, entry.sortPriority());
    }

    private void updateCustomEntryDisplay(Player viewer, UUID uuid, String profileName, CustomTablistEntry entry, Component display) {
        FS_GameProfile profile = new FS_GameProfile(uuid, profileName);
        if (entry.skin() != null) {
            entry.skin().applyTo(profile);
        }

        FS_GameType gameMode = TablistUtils.toGameType(entry.gameMode());
        int ping = Math.max(0, entry.ping());

        FS_ClientboundPlayerInfoUpdatePacket.Entry packetEntry = new FS_ClientboundPlayerInfoUpdatePacket.Entry(
                uuid,
                profile,
                true,
                ping,
                gameMode,
                display
        );

        FS_ClientboundPlayerInfoUpdatePacket packet = FancySitula.PACKET_FACTORY.createPlayerInfoUpdatePacket(
                EnumSet.of(FS_ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME),
                List.of(packetEntry)
        );

        packet.send(new FS_RealPlayer(viewer));
    }

    private void removeCustomEntry(Player viewer, CustomEntryState state) {
        FS_ClientboundPlayerInfoRemovePacket packet = FancySitula.PACKET_FACTORY.createPlayerInfoRemovePacket(List.of(state.uuid));
        packet.send(new FS_RealPlayer(viewer));
        teamManager.removeEntity(viewer, state.profileName);
    }

    private boolean skinsMatch(CustomEntryState state, CustomTablistEntry entry) {
        if (state.skin == null && entry.skin() == null) {
            return true;
        }
        if (state.skin == null || entry.skin() == null) {
            return false;
        }
        return state.skin.equals(entry.skin());
    }

    private static class HeaderFooterState {
        private final Component header;
        private final Component footer;

        private HeaderFooterState(Component header, Component footer) {
            this.header = header;
            this.footer = footer;
        }
    }

    private static class CustomEntryState {
        private final UUID uuid;
        private final String profileName;
        private Component display;
        private int sortPriority;
        private final TablistSkin skin;

        private CustomEntryState(UUID uuid, String profileName, Component display, int sortPriority, TablistSkin skin) {
            this.uuid = uuid;
            this.profileName = profileName;
            this.display = display;
            this.sortPriority = sortPriority;
            this.skin = skin;
        }
    }
}
