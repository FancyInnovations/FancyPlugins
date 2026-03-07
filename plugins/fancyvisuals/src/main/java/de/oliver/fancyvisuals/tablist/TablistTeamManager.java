package de.oliver.fancyvisuals.tablist;

import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.packets.FS_Color;
import de.oliver.fancysitula.api.teams.FS_CollisionRule;
import de.oliver.fancysitula.api.teams.FS_NameTagVisibility;
import de.oliver.fancysitula.api.teams.FS_Team;
import de.oliver.fancysitula.factories.FancySitula;
import de.oliver.fancyvisuals.FancyVisuals;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TablistTeamManager {

    private final Map<UUID, Map<String, String>> viewerAssignments;
    private final Map<UUID, Set<String>> viewerTeams;

    public TablistTeamManager() {
        this.viewerAssignments = new HashMap<>();
        this.viewerTeams = new HashMap<>();
    }

    public void assignEntity(Player viewer, String entityName, int sortPriority) {
        if (viewer == null || !viewer.isOnline()) {
            return;
        }

        String teamName = getTeamName(sortPriority);
        Map<String, String> assignments = viewerAssignments.computeIfAbsent(viewer.getUniqueId(), key -> new HashMap<>());
        String currentTeam = assignments.get(entityName);
        if (teamName.equals(currentTeam)) {
            return;
        }

        FS_RealPlayer fsViewer = new FS_RealPlayer(viewer);

        if (currentTeam != null) {
            FS_Team oldTeam = buildTeam(currentTeam);
            FancySitula.TEAM_FACTORY.removeEntitiesFromTeamFor(fsViewer, oldTeam, List.of(entityName));
        }

        ensureTeam(viewer, teamName);

        FS_Team team = buildTeam(teamName);
        FancySitula.TEAM_FACTORY.addEntitiesToTeamFor(fsViewer, team, List.of(entityName));
        assignments.put(entityName, teamName);
    }

    public void removeEntity(Player viewer, String entityName) {
        if (viewer == null || !viewer.isOnline()) {
            return;
        }

        Map<String, String> assignments = viewerAssignments.get(viewer.getUniqueId());
        if (assignments == null) {
            return;
        }

        String teamName = assignments.remove(entityName);
        if (teamName == null) {
            return;
        }

        FS_Team team = buildTeam(teamName);
        FS_RealPlayer fsViewer = new FS_RealPlayer(viewer);
        FancySitula.TEAM_FACTORY.removeEntitiesFromTeamFor(fsViewer, team, List.of(entityName));
    }

    public void removeViewer(Player viewer) {
        if (viewer == null) {
            return;
        }

        viewerAssignments.remove(viewer.getUniqueId());
        viewerTeams.remove(viewer.getUniqueId());
    }

    private void ensureTeam(Player viewer, String teamName) {
        Set<String> created = viewerTeams.computeIfAbsent(viewer.getUniqueId(), key -> new HashSet<>());
        if (created.contains(teamName)) {
            return;
        }

        FS_Team team = buildTeam(teamName);
        FS_RealPlayer fsViewer = new FS_RealPlayer(viewer);
        FancySitula.TEAM_FACTORY.createTeamFor(fsViewer, team);
        created.add(teamName);
    }

    private FS_Team buildTeam(String teamName) {
        boolean hideNametag = FancyVisuals.get().getNametagConfig().hideVanillaNametag();
        FS_NameTagVisibility visibility = hideNametag ? FS_NameTagVisibility.NEVER : FS_NameTagVisibility.ALWAYS;
        return new FS_Team(
                teamName,
                Component.text(teamName),
                true,
                true,
                visibility,
                FS_CollisionRule.ALWAYS,
                FS_Color.WHITE,
                Component.empty(),
                Component.empty(),
                List.of()
        );
    }

    private String getTeamName(int sortPriority) {
        int normalized = Math.max(0, Math.min(999, sortPriority));
        return "fv" + String.format("%03d", normalized);
    }
}
