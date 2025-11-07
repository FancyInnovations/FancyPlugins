package de.oliver.fancysitula.versions.v1_21_11.packets;

import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.packets.FS_ClientboundCreateOrUpdateTeamPacket;
import de.oliver.fancysitula.versions.v1_21_11.utils.VanillaPlayerAdapter;
import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

public class ClientboundCreateOrUpdateTeamPacketImpl extends FS_ClientboundCreateOrUpdateTeamPacket {

    private static final Scoreboard SCOREBOARD = new Scoreboard();

    public ClientboundCreateOrUpdateTeamPacketImpl(String teamName, CreateTeam createTeam) {
        super(teamName, createTeam);
    }

    public ClientboundCreateOrUpdateTeamPacketImpl(String teamName, RemoveTeam removeTeam) {
        super(teamName, removeTeam);
    }

    public ClientboundCreateOrUpdateTeamPacketImpl(String teamName, UpdateTeam updateTeam) {
        super(teamName, updateTeam);
    }

    public ClientboundCreateOrUpdateTeamPacketImpl(String teamName, AddEntity addEntity) {
        super(teamName, addEntity);
    }

    public ClientboundCreateOrUpdateTeamPacketImpl(String teamName, RemoveEntity removeEntity) {
        super(teamName, removeEntity);
    }

    @Override
    public Object createPacket() {
        return switch (method) {
            case CREATE_TEAM -> createCreateTeamPacket();
            case REMOVE_TEAM -> createRemoveTeamPacket();
            case UPDATE_TEAM -> createUpdateTeamPacket();
            case ADD_ENTITY -> createAddEntityPacket();
            case REMOVE_ENTITY -> createRemoveEntityPacket();
        };
    }

    private Object createCreateTeamPacket() {
        if (createTeam == null) {
            return null;
        }

        PlayerTeam playerTeam = new PlayerTeam(SCOREBOARD, teamName);
        playerTeam.setDisplayName(PaperAdventure.asVanilla(createTeam.getDisplayName()));
        playerTeam.setAllowFriendlyFire(createTeam.isAllowFriendlyFire());
        playerTeam.setSeeFriendlyInvisibles(createTeam.isCanSeeFriendlyInvisibles());
        playerTeam.setNameTagVisibility(Team.Visibility.valueOf(createTeam.getNameTagVisibility().getName()));
        playerTeam.setCollisionRule(PlayerTeam.CollisionRule.valueOf(createTeam.getCollisionRule().getName()));
        playerTeam.setColor(ChatFormatting.getById(createTeam.getColor().getId()));
        playerTeam.setPlayerPrefix(PaperAdventure.asVanilla(createTeam.getPrefix()));
        playerTeam.setPlayerSuffix(PaperAdventure.asVanilla(createTeam.getSuffix()));
        for (String entity : createTeam.getEntities()) {
            playerTeam.getPlayers().add(entity);
        }

        return ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(playerTeam, true);
    }

    private Object createRemoveTeamPacket() {
        if (removeTeam == null) {
            return null;
        }

        PlayerTeam playerTeam = new PlayerTeam(SCOREBOARD, teamName);
        return ClientboundSetPlayerTeamPacket.createRemovePacket(playerTeam);
    }

    private Object createUpdateTeamPacket() {
        if (updateTeam == null) {
            return null;
        }

        PlayerTeam playerTeam = new PlayerTeam(SCOREBOARD, teamName);
        playerTeam.setDisplayName(PaperAdventure.asVanilla(updateTeam.getDisplayName()));
        playerTeam.setAllowFriendlyFire(updateTeam.isAllowFriendlyFire());
        playerTeam.setSeeFriendlyInvisibles(updateTeam.isCanSeeFriendlyInvisibles());
        playerTeam.setNameTagVisibility(Team.Visibility.valueOf(updateTeam.getNameTagVisibility().getName()));
        playerTeam.setCollisionRule(PlayerTeam.CollisionRule.valueOf(updateTeam.getCollisionRule().getName()));
        playerTeam.setColor(ChatFormatting.getById(updateTeam.getColor().getId()));
        playerTeam.setPlayerPrefix(PaperAdventure.asVanilla(updateTeam.getPrefix()));
        playerTeam.setPlayerSuffix(PaperAdventure.asVanilla(updateTeam.getSuffix()));

        return ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(playerTeam, true);
    }

    private Object createAddEntityPacket() {
        if (addEntity == null) {
            return null;
        }

        PlayerTeam playerTeam = new PlayerTeam(SCOREBOARD, teamName);
        for (String entity : addEntity.getEntities()) {
            playerTeam.getPlayers().add(entity);
        }
        return ClientboundSetPlayerTeamPacket.createMultiplePlayerPacket(playerTeam, addEntity.getEntities(), ClientboundSetPlayerTeamPacket.Action.ADD);
    }

    private Object createRemoveEntityPacket() {
        if (removeEntity == null) {
            return null;
        }

        PlayerTeam playerTeam = new PlayerTeam(SCOREBOARD, teamName);
        for (String entity : removeEntity.getEntities()) {
            playerTeam.getPlayers().add(entity);
        }
        return ClientboundSetPlayerTeamPacket.createMultiplePlayerPacket(playerTeam, removeEntity.getEntities(), ClientboundSetPlayerTeamPacket.Action.REMOVE);
    }

    @Override
    protected void sendPacketTo(FS_RealPlayer player) {
        ClientboundSetPlayerTeamPacket packet = (ClientboundSetPlayerTeamPacket) createPacket();

        ServerPlayer vanillaPlayer = VanillaPlayerAdapter.asVanilla(player.getBukkitPlayer());
        vanillaPlayer.connection.send(packet);
    }
}
