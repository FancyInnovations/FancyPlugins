package de.oliver.fancysitula.factories;

import de.oliver.fancysitula.api.dialogs.FS_Dialog;
import de.oliver.fancysitula.api.packets.*;
import de.oliver.fancysitula.api.utils.FS_EquipmentSlot;
import de.oliver.fancysitula.api.utils.ServerVersion;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Factory class for creating packet instances based on the server version
 */
public class PacketFactory {

    /**
     * Creates a new FS_ClientboundPlayerInfoUpdatePacket instance based on the server version
     *
     * @param actions EnumSet of {@link FS_ClientboundPlayerInfoUpdatePacket.Action} to perform
     * @param entries List of {@link FS_ClientboundPlayerInfoUpdatePacket.Entry} to update
     */
    public FS_ClientboundPlayerInfoUpdatePacket createPlayerInfoUpdatePacket(
            EnumSet<FS_ClientboundPlayerInfoUpdatePacket.Action> actions,
            List<FS_ClientboundPlayerInfoUpdatePacket.Entry> entries
    ) {
        switch (ServerVersion.getCurrentVersion()) {
            case v1_21_11 -> {
                return new de.oliver.fancysitula.versions.v1_21_11.packets.ClientboundPlayerInfoUpdatePacketImpl(actions, entries);
            }
            case v1_21_9, v1_21_10 -> {
                return new de.oliver.fancysitula.versions.v1_21_9.packets.ClientboundPlayerInfoUpdatePacketImpl(actions, entries);
            }
            case v1_21_6, v1_21_7, v1_21_8 -> {
                return new de.oliver.fancysitula.versions.v1_21_6.packets.ClientboundPlayerInfoUpdatePacketImpl(actions, entries);
            }
            case v1_21_5 -> {
                return new de.oliver.fancysitula.versions.v1_21_5.packets.ClientboundPlayerInfoUpdatePacketImpl(actions, entries);
            }
            case v1_21_4 -> {
                return new de.oliver.fancysitula.versions.v1_21_4.packets.ClientboundPlayerInfoUpdatePacketImpl(actions, entries);
            }
            case v1_21_3 -> {
                return new de.oliver.fancysitula.versions.v1_21_3.packets.ClientboundPlayerInfoUpdatePacketImpl(actions, entries);
            }
            case v1_21, v1_21_1 -> {
                return new de.oliver.fancysitula.versions.v1_20_6.packets.ClientboundPlayerInfoUpdatePacketImpl(actions, entries);
            }
            default ->
                    throw new IllegalArgumentException("Unsupported server version: " + ServerVersion.getCurrentVersion());
        }
    }

    /**
     * Creates a new FS_ClientboundAddEntityPacket instance based on the server version
     *
     * @param yaw     in degrees (0 - 360)
     * @param pitch   in degrees (0 - 360)
     * @param headYaw in degrees (0 - 360)
     */
    public FS_ClientboundAddEntityPacket createAddEntityPacket(
            int entityId,
            UUID entityUUID,
            EntityType entityType,
            double x,
            double y,
            double z,
            float yaw,
            float pitch,
            float headYaw,
            int velocityX,
            int velocityY,
            int velocityZ,
            int data) {
        switch (ServerVersion.getCurrentVersion()) {
            case v1_21_11 -> {
                return new de.oliver.fancysitula.versions.v1_21_11.packets.ClientboundAddEntityPacketImpl(entityId, entityUUID, entityType, x, y, z, yaw, pitch, headYaw, velocityX, velocityY, velocityZ, data);
            }
            case v1_21_9, v1_21_10 -> {
                return new de.oliver.fancysitula.versions.v1_21_9.packets.ClientboundAddEntityPacketImpl(entityId, entityUUID, entityType, x, y, z, yaw, pitch, headYaw, velocityX, velocityY, velocityZ, data);
            }
            case v1_21_6, v1_21_7, v1_21_8 -> {
                return new de.oliver.fancysitula.versions.v1_21_6.packets.ClientboundAddEntityPacketImpl(entityId, entityUUID, entityType, x, y, z, yaw, pitch, headYaw, velocityX, velocityY, velocityZ, data);
            }
            case v1_21_5 -> {
                return new de.oliver.fancysitula.versions.v1_21_5.packets.ClientboundAddEntityPacketImpl(entityId, entityUUID, entityType, x, y, z, yaw, pitch, headYaw, velocityX, velocityY, velocityZ, data);
            }
            case v1_21_4 -> {
                return new de.oliver.fancysitula.versions.v1_21_4.packets.ClientboundAddEntityPacketImpl(entityId, entityUUID, entityType, x, y, z, yaw, pitch, headYaw, velocityX, velocityY, velocityZ, data);
            }
            case v1_21_3 -> {
                return new de.oliver.fancysitula.versions.v1_21_3.packets.ClientboundAddEntityPacketImpl(entityId, entityUUID, entityType, x, y, z, yaw, pitch, headYaw, velocityX, velocityY, velocityZ, data);
            }
            case v1_21, v1_21_1 -> {
                return new de.oliver.fancysitula.versions.v1_20_6.packets.ClientboundAddEntityPacketImpl(entityId, entityUUID, entityType, x, y, z, yaw, pitch, headYaw, velocityX, velocityY, velocityZ, data);
            }
            default ->
                    throw new IllegalArgumentException("Unsupported server version: " + ServerVersion.getCurrentVersion());
        }
    }

    /**
     * Creates a new FS_ClientboundPlayerInfoRemovePacket instance based on the server version
     *
     * @param uuids UUIDs of the players to remove
     */
    public FS_ClientboundPlayerInfoRemovePacket createPlayerInfoRemovePacket(
            List<UUID> uuids
    ) {
        switch (ServerVersion.getCurrentVersion()) {
            case v1_21_11 -> {
                return new de.oliver.fancysitula.versions.v1_21_11.packets.ClientboundPlayerInfoRemovePacketImpl(uuids);
            }
            case v1_21_9, v1_21_10 -> {
                return new de.oliver.fancysitula.versions.v1_21_9.packets.ClientboundPlayerInfoRemovePacketImpl(uuids);
            }
            case v1_21_6, v1_21_7, v1_21_8 -> {
                return new de.oliver.fancysitula.versions.v1_21_6.packets.ClientboundPlayerInfoRemovePacketImpl(uuids);
            }
            case v1_21_5 -> {
                return new de.oliver.fancysitula.versions.v1_21_5.packets.ClientboundPlayerInfoRemovePacketImpl(uuids);
            }
            case v1_21_4 -> {
                return new de.oliver.fancysitula.versions.v1_21_4.packets.ClientboundPlayerInfoRemovePacketImpl(uuids);
            }
            case v1_21_3 -> {
                return new de.oliver.fancysitula.versions.v1_21_3.packets.ClientboundPlayerInfoRemovePacketImpl(uuids);
            }
            case v1_21, v1_21_1 -> {
                return new de.oliver.fancysitula.versions.v1_20_6.packets.ClientboundPlayerInfoRemovePacketImpl(uuids);
            }
            default ->
                    throw new IllegalArgumentException("Unsupported server version: " + ServerVersion.getCurrentVersion());
        }
    }

    /**
     * Creates a new FS_ClientboundRemoveEntitiesPacket instance based on the server version
     *
     * @param entityIds IDs of the entities to remove
     */
    public FS_ClientboundRemoveEntitiesPacket createRemoveEntitiesPacket(
            List<Integer> entityIds
    ) {
        switch (ServerVersion.getCurrentVersion()) {
            case v1_21_11 -> {
                return new de.oliver.fancysitula.versions.v1_21_11.packets.ClientboundRemoveEntitiesPacketImpl(entityIds);
            }
            case v1_21_9, v1_21_10 -> {
                return new de.oliver.fancysitula.versions.v1_21_9.packets.ClientboundRemoveEntitiesPacketImpl(entityIds);
            }
            case v1_21_6, v1_21_7, v1_21_8 -> {
                return new de.oliver.fancysitula.versions.v1_21_6.packets.ClientboundRemoveEntitiesPacketImpl(entityIds);
            }
            case v1_21_5 -> {
                return new de.oliver.fancysitula.versions.v1_21_5.packets.ClientboundRemoveEntitiesPacketImpl(entityIds);
            }
            case v1_21_4 -> {
                return new de.oliver.fancysitula.versions.v1_21_4.packets.ClientboundRemoveEntitiesPacketImpl(entityIds);
            }
            case v1_21_3 -> {
                return new de.oliver.fancysitula.versions.v1_21_3.packets.ClientboundRemoveEntitiesPacketImpl(entityIds);
            }
            case v1_21, v1_21_1 -> {
                return new de.oliver.fancysitula.versions.v1_20_6.packets.ClientboundRemoveEntitiesPacketImpl(entityIds);
            }
            default ->
                    throw new IllegalArgumentException("Unsupported server version: " + ServerVersion.getCurrentVersion());
        }
    }

    /**
     * Creates a new FS_ClientboundTeleportEntityPacket instance based on the server version
     *
     * @param entityId ID of the entity to teleport
     * @param x        X coordinate
     * @param y        Y coordinate
     * @param z        Z coordinate
     * @param yaw      Yaw in degrees (0 - 360)
     * @param pitch    Pitch in degrees (0 - 360)
     * @param onGround Whether the entity is on the ground
     */
    public FS_ClientboundTeleportEntityPacket createTeleportEntityPacket(
            int entityId,
            double x,
            double y,
            double z,
            float yaw,
            float pitch,
            boolean onGround
    ) {
        switch (ServerVersion.getCurrentVersion()) {
            case v1_21_11 -> {
                return new de.oliver.fancysitula.versions.v1_21_11.packets.ClientboundTeleportEntityPacketImpl(entityId, x, y, z, yaw, pitch, onGround);
            }
            case v1_21_9, v1_21_10 -> {
                return new de.oliver.fancysitula.versions.v1_21_9.packets.ClientboundTeleportEntityPacketImpl(entityId, x, y, z, yaw, pitch, onGround);
            }
            case v1_21_6, v1_21_7, v1_21_8 -> {
                return new de.oliver.fancysitula.versions.v1_21_6.packets.ClientboundTeleportEntityPacketImpl(entityId, x, y, z, yaw, pitch, onGround);
            }
            case v1_21_5 -> {
                return new de.oliver.fancysitula.versions.v1_21_5.packets.ClientboundTeleportEntityPacketImpl(entityId, x, y, z, yaw, pitch, onGround);
            }
            case v1_21_4 -> {
                return new de.oliver.fancysitula.versions.v1_21_4.packets.ClientboundTeleportEntityPacketImpl(entityId, x, y, z, yaw, pitch, onGround);
            }
            case v1_21_3 -> {
                return new de.oliver.fancysitula.versions.v1_21_3.packets.ClientboundTeleportEntityPacketImpl(entityId, x, y, z, yaw, pitch, onGround);
            }
            case v1_21, v1_21_1 -> {
                return new de.oliver.fancysitula.versions.v1_20_6.packets.ClientboundTeleportEntityPacketImpl(entityId, x, y, z, yaw, pitch, onGround);
            }
            default ->
                    throw new IllegalArgumentException("Unsupported server version: " + ServerVersion.getCurrentVersion());
        }
    }

    /**
     * Creates a new FS_ClientboundRotateHeadPacket instance based on the server version
     *
     * @param entityId ID of the entity to rotate the head of
     * @param headYaw  Yaw of the head in degrees (0 - 360)
     */
    public FS_ClientboundRotateHeadPacket createRotateHeadPacket(
            int entityId,
            float headYaw
    ) {
        switch (ServerVersion.getCurrentVersion()) {
            case v1_21_11 -> {
                return new de.oliver.fancysitula.versions.v1_21_11.packets.ClientboundRotateHeadPacketImpl(entityId, headYaw);
            }
            case v1_21_9, v1_21_10 -> {
                return new de.oliver.fancysitula.versions.v1_21_9.packets.ClientboundRotateHeadPacketImpl(entityId, headYaw);
            }
            case v1_21_6, v1_21_7, v1_21_8 -> {
                return new de.oliver.fancysitula.versions.v1_21_6.packets.ClientboundRotateHeadPacketImpl(entityId, headYaw);
            }
            case v1_21_5 -> {
                return new de.oliver.fancysitula.versions.v1_21_5.packets.ClientboundRotateHeadPacketImpl(entityId, headYaw);
            }
            case v1_21_4 -> {
                return new de.oliver.fancysitula.versions.v1_21_4.packets.ClientboundRotateHeadPacketImpl(entityId, headYaw);
            }
            case v1_21_3 -> {
                return new de.oliver.fancysitula.versions.v1_21_3.packets.ClientboundRotateHeadPacketImpl(entityId, headYaw);
            }
            case v1_21, v1_21_1 -> {
                return new de.oliver.fancysitula.versions.v1_20_6.packets.ClientboundRotateHeadPacketImpl(entityId, headYaw);
            }
            default ->
                    throw new IllegalArgumentException("Unsupported server version: " + ServerVersion.getCurrentVersion());
        }
    }

    /**
     * Creates a new FS_ClientboundSetEntityDataPacket instance based on the server version
     *
     * @param entityId   ID of the entity to set the data of
     * @param entityData List of {@link FS_ClientboundSetEntityDataPacket.EntityData} to set
     */
    public FS_ClientboundSetEntityDataPacket createSetEntityDataPacket(
            int entityId,
            List<FS_ClientboundSetEntityDataPacket.EntityData> entityData
    ) {
        switch (ServerVersion.getCurrentVersion()) {
            case v1_21_11 -> {
                return new de.oliver.fancysitula.versions.v1_21_11.packets.ClientboundSetEntityDataPacketImpl(entityId, entityData);
            }
            case v1_21_9, v1_21_10 -> {
                return new de.oliver.fancysitula.versions.v1_21_9.packets.ClientboundSetEntityDataPacketImpl(entityId, entityData);
            }
            case v1_21_6, v1_21_7, v1_21_8 -> {
                return new de.oliver.fancysitula.versions.v1_21_6.packets.ClientboundSetEntityDataPacketImpl(entityId, entityData);
            }
            case v1_21_5 -> {
                return new de.oliver.fancysitula.versions.v1_21_5.packets.ClientboundSetEntityDataPacketImpl(entityId, entityData);
            }
            case v1_21_4 -> {
                return new de.oliver.fancysitula.versions.v1_21_4.packets.ClientboundSetEntityDataPacketImpl(entityId, entityData);
            }
            case v1_21_3 -> {
                return new de.oliver.fancysitula.versions.v1_21_3.packets.ClientboundSetEntityDataPacketImpl(entityId, entityData);
            }
            case v1_21, v1_21_1 -> {
                return new de.oliver.fancysitula.versions.v1_20_6.packets.ClientboundSetEntityDataPacketImpl(entityId, entityData);
            }
            default ->
                    throw new IllegalArgumentException("Unsupported server version: " + ServerVersion.getCurrentVersion());
        }
    }

    /**
     * Creates a new FS_ClientboundSetEquipmentPacket instance based on the server version
     *
     * @param entityId  ID of the entity to set the equipment of
     * @param equipment Map of {@link org.bukkit.inventory.EquipmentSlot} and {@link org.bukkit.inventory.ItemStack} to set
     */
    public FS_ClientboundSetEquipmentPacket createSetEquipmentPacket(
            int entityId,
            Map<FS_EquipmentSlot, ItemStack> equipment
    ) {
        switch (ServerVersion.getCurrentVersion()) {
            case v1_21_11 -> {
                return new de.oliver.fancysitula.versions.v1_21_11.packets.ClientboundSetEquipmentPacketImpl(entityId, equipment);
            }
            case v1_21_9, v1_21_10 -> {
                return new de.oliver.fancysitula.versions.v1_21_9.packets.ClientboundSetEquipmentPacketImpl(entityId, equipment);
            }
            case v1_21_6, v1_21_7, v1_21_8 -> {
                return new de.oliver.fancysitula.versions.v1_21_6.packets.ClientboundSetEquipmentPacketImpl(entityId, equipment);
            }
            case v1_21_5 -> {
                return new de.oliver.fancysitula.versions.v1_21_5.packets.ClientboundSetEquipmentPacketImpl(entityId, equipment);
            }
            case v1_21_4 -> {
                return new de.oliver.fancysitula.versions.v1_21_4.packets.ClientboundSetEquipmentPacketImpl(entityId, equipment);
            }
            case v1_21_3 -> {
                return new de.oliver.fancysitula.versions.v1_21_3.packets.ClientboundSetEquipmentPacketImpl(entityId, equipment);
            }
            case v1_21, v1_21_1 -> {
                return new de.oliver.fancysitula.versions.v1_20_6.packets.ClientboundSetEquipmentPacketImpl(entityId, equipment);
            }
            default ->
                    throw new IllegalArgumentException("Unsupported server version: " + ServerVersion.getCurrentVersion());
        }
    }

    /**
     * Creates a new FS_ClientboundSetPassengersPacket instance based on the server version
     *
     * @param entityId   ID of the vehicle entity
     * @param passengers List of entity IDs to set as passengers
     */
    public FS_ClientboundSetPassengersPacket createSetPassengersPacket(
            int entityId,
            List<Integer> passengers
    ) {
        switch (ServerVersion.getCurrentVersion()) {
            case v1_21_11 -> {
                return new de.oliver.fancysitula.versions.v1_21_11.packets.ClientboundSetPassengersPacketImpl(entityId, passengers);
            }
            case v1_21_9, v1_21_10 -> {
                return new de.oliver.fancysitula.versions.v1_21_9.packets.ClientboundSetPassengersPacketImpl(entityId, passengers);
            }
            case v1_21_6, v1_21_7, v1_21_8 -> {
                return new de.oliver.fancysitula.versions.v1_21_6.packets.ClientboundSetPassengersPacketImpl(entityId, passengers);
            }
            case v1_21_5 -> {
                return new de.oliver.fancysitula.versions.v1_21_5.packets.ClientboundSetPassengersPacketImpl(entityId, passengers);
            }
            case v1_21_4 -> {
                return new de.oliver.fancysitula.versions.v1_21_4.packets.ClientboundSetPassengersPacketImpl(entityId, passengers);
            }
            case v1_21_3 -> {
                return new de.oliver.fancysitula.versions.v1_21_3.packets.ClientboundSetPassengersPacketImpl(entityId, passengers);
            }
            case v1_21, v1_21_1 -> {
                return new de.oliver.fancysitula.versions.v1_20_6.packets.ClientboundSetPassengersPacketImpl(entityId, passengers);
            }
            default ->
                    throw new IllegalArgumentException("Unsupported server version: " + ServerVersion.getCurrentVersion());
        }
    }


    /**
     * Creates and returns a FS_ClientboundCreateOrUpdateTeamPacket based on the given server version and team information.
     *
     * @param teamName   the name of the team to be created or updated
     * @param createTeam an instance of FS_ClientboundCreateOrUpdateTeamPacket.CreateTeam containing the team creation or update details
     * @return a FS_ClientboundCreateOrUpdateTeamPacket instance corresponding to the specified server version and team details
     * @throws IllegalArgumentException if the provided server version is not supported
     */
    public FS_ClientboundCreateOrUpdateTeamPacket createCreateOrUpdateTeamPacket(
            String teamName,
            FS_ClientboundCreateOrUpdateTeamPacket.CreateTeam createTeam
    ) {
        switch (ServerVersion.getCurrentVersion()) {
            case v1_21_11 -> {
                return new de.oliver.fancysitula.versions.v1_21_11.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, createTeam);
            }
            case v1_21_9, v1_21_10 -> {
                return new de.oliver.fancysitula.versions.v1_21_9.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, createTeam);
            }
            case v1_21_6, v1_21_7, v1_21_8 -> {
                return new de.oliver.fancysitula.versions.v1_21_6.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, createTeam);
            }
            case v1_21_5 -> {
                return new de.oliver.fancysitula.versions.v1_21_5.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, createTeam);
            }
            case v1_21_4 -> {
                return new de.oliver.fancysitula.versions.v1_21_4.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, createTeam);
            }
            case v1_21_3 -> {
                return new de.oliver.fancysitula.versions.v1_21_3.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, createTeam);
            }
            case v1_21, v1_21_1 -> {
                return new de.oliver.fancysitula.versions.v1_20_6.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, createTeam);
            }
            default ->
                    throw new IllegalArgumentException("Unsupported server version: " + ServerVersion.getCurrentVersion());
        }
    }

    /**
     * Creates a packet for creating or updating a team based on the specified server version.
     *
     * @param teamName   The name of the team.
     * @param removeTeam Information about whether to remove the team.
     * @return The packet for creating or updating the team.
     * @throws IllegalArgumentException if the server version is unsupported.
     */
    public FS_ClientboundCreateOrUpdateTeamPacket createCreateOrUpdateTeamPacket(
            String teamName,
            FS_ClientboundCreateOrUpdateTeamPacket.RemoveTeam removeTeam
    ) {
        switch (ServerVersion.getCurrentVersion()) {
            case v1_21_11 -> {
                return new de.oliver.fancysitula.versions.v1_21_11.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, removeTeam);
            }
            case v1_21_9, v1_21_10 -> {
                return new de.oliver.fancysitula.versions.v1_21_9.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, removeTeam);
            }
            case v1_21_6, v1_21_7, v1_21_8 -> {
                return new de.oliver.fancysitula.versions.v1_21_6.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, removeTeam);
            }
            case v1_21_5 -> {
                return new de.oliver.fancysitula.versions.v1_21_5.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, removeTeam);
            }
            case v1_21_4 -> {
                return new de.oliver.fancysitula.versions.v1_21_4.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, removeTeam);
            }
            case v1_21_3 -> {
                return new de.oliver.fancysitula.versions.v1_21_3.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, removeTeam);
            }
            case v1_21, v1_21_1 -> {
                return new de.oliver.fancysitula.versions.v1_20_6.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, removeTeam);
            }
            default ->
                    throw new IllegalArgumentException("Unsupported server version: " + ServerVersion.getCurrentVersion());
        }
    }

    /**
     * Creates an instance of FS_ClientboundCreateOrUpdateTeamPacket based on the provided server version.
     *
     * @param teamName   The name of the team that is being created or updated.
     * @param updateTeam The update team details which contain information about the team.
     * @return A new instance of FS_ClientboundCreateOrUpdateTeamPacket tailored for the specified server version.
     * @throws IllegalArgumentException If the provided server version is not supported.
     */
    public FS_ClientboundCreateOrUpdateTeamPacket createCreateOrUpdateTeamPacket(
            String teamName,
            FS_ClientboundCreateOrUpdateTeamPacket.UpdateTeam updateTeam
    ) {
        switch (ServerVersion.getCurrentVersion()) {
            case v1_21_11 -> {
                return new de.oliver.fancysitula.versions.v1_21_11.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, updateTeam);
            }
            case v1_21_9, v1_21_10 -> {
                return new de.oliver.fancysitula.versions.v1_21_9.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, updateTeam);
            }
            case v1_21_6, v1_21_7, v1_21_8 -> {
                return new de.oliver.fancysitula.versions.v1_21_6.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, updateTeam);
            }
            case v1_21_5 -> {
                return new de.oliver.fancysitula.versions.v1_21_5.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, updateTeam);
            }
            case v1_21_4 -> {
                return new de.oliver.fancysitula.versions.v1_21_4.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, updateTeam);
            }
            case v1_21_3 -> {
                return new de.oliver.fancysitula.versions.v1_21_3.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, updateTeam);
            }
            case v1_21, v1_21_1 -> {
                return new de.oliver.fancysitula.versions.v1_20_6.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, updateTeam);
            }
            default ->
                    throw new IllegalArgumentException("Unsupported server version: " + ServerVersion.getCurrentVersion());
        }
    }

    /**
     * Creates a new instance of FS_ClientboundCreateOrUpdateTeamPacket based on the given server version, team name, and addEntity parameters.
     *
     * @param teamName  the name of the team to be created or updated
     * @param addEntity the add entity information needed for packet creation
     * @return an instance of FS_ClientboundCreateOrUpdateTeamPacket appropriate for the specified server version
     * @throws IllegalArgumentException if the server version is not supported
     */
    public FS_ClientboundCreateOrUpdateTeamPacket createCreateOrUpdateTeamPacket(
            String teamName,
            FS_ClientboundCreateOrUpdateTeamPacket.AddEntity addEntity
    ) {
        switch (ServerVersion.getCurrentVersion()) {
            case v1_21_11 -> {
                return new de.oliver.fancysitula.versions.v1_21_11.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, addEntity);
            }
            case v1_21_9, v1_21_10 -> {
                return new de.oliver.fancysitula.versions.v1_21_9.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, addEntity);
            }
            case v1_21_6, v1_21_7, v1_21_8 -> {
                return new de.oliver.fancysitula.versions.v1_21_6.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, addEntity);
            }
            case v1_21_5 -> {
                return new de.oliver.fancysitula.versions.v1_21_5.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, addEntity);
            }
            case v1_21_4 -> {
                return new de.oliver.fancysitula.versions.v1_21_4.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, addEntity);
            }
            case v1_21_3 -> {
                return new de.oliver.fancysitula.versions.v1_21_3.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, addEntity);
            }
            case v1_21, v1_21_1 -> {
                return new de.oliver.fancysitula.versions.v1_20_6.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, addEntity);
            }
            default ->
                    throw new IllegalArgumentException("Unsupported server version: " + ServerVersion.getCurrentVersion());
        }
    }

    /**
     * Creates an instance of FS_ClientboundCreateOrUpdateTeamPacket based on the server version.
     *
     * @param teamName     The name of the team to create or update.
     * @param removeEntity The entity removal configuration for the packet.
     * @return A new instance of FS_ClientboundCreateOrUpdateTeamPacket for the specified server version.
     * @throws IllegalArgumentException If the server version is unsupported.
     */
    public FS_ClientboundCreateOrUpdateTeamPacket createCreateOrUpdateTeamPacket(
            String teamName,
            FS_ClientboundCreateOrUpdateTeamPacket.RemoveEntity removeEntity
    ) {
        switch (ServerVersion.getCurrentVersion()) {
            case v1_21_11 -> {
                return new de.oliver.fancysitula.versions.v1_21_11.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, removeEntity);
            }
            case v1_21_9, v1_21_10 -> {
                return new de.oliver.fancysitula.versions.v1_21_9.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, removeEntity);
            }
            case v1_21_6, v1_21_7, v1_21_8 -> {
                return new de.oliver.fancysitula.versions.v1_21_6.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, removeEntity);
            }
            case v1_21_5 -> {
                return new de.oliver.fancysitula.versions.v1_21_5.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, removeEntity);
            }
            case v1_21_4 -> {
                return new de.oliver.fancysitula.versions.v1_21_4.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, removeEntity);
            }
            case v1_21_3 -> {
                return new de.oliver.fancysitula.versions.v1_21_3.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, removeEntity);
            }
            case v1_21, v1_21_1 -> {
                return new de.oliver.fancysitula.versions.v1_20_6.packets.ClientboundCreateOrUpdateTeamPacketImpl(teamName, removeEntity);
            }
            default ->
                    throw new IllegalArgumentException("Unsupported server version: " + ServerVersion.getCurrentVersion());
        }
    }

    /**
     * Creates a new FS_ClientboundShowDialogPacket instance based on the server version
     *
     * @param dialog The dialog to show
     * @return An instance of FS_ClientboundShowDialogPacket for the specified server version
     */
    public FS_ClientboundShowDialogPacket createShowDialogPacket(
            FS_Dialog dialog
    ) {
        switch (ServerVersion.getCurrentVersion()) {
            case v1_21_11 -> {
                return new de.oliver.fancysitula.versions.v1_21_11.packets.ClientboundShowDialogPacketImpl(dialog);
            }
            case v1_21_9, v1_21_10 -> {
                return new de.oliver.fancysitula.versions.v1_21_9.packets.ClientboundShowDialogPacketImpl(dialog);
            }
            case v1_21_6, v1_21_7, v1_21_8 -> {
                return new de.oliver.fancysitula.versions.v1_21_6.packets.ClientboundShowDialogPacketImpl(dialog);
            }
            default ->
                    throw new IllegalArgumentException("Unsupported server version: " + ServerVersion.getCurrentVersion());
        }
    }

    /**
     * Creates a new FS_ClientboundClearDialogPacket instance based on the server version
     *
     * @return An instance of FS_ClientboundClearDialogPacket for the specified server version
     */
    public FS_ClientboundClearDialogPacket createClearDialogPacket() {
        switch (ServerVersion.getCurrentVersion()) {
            case v1_21_11 -> {
                return new de.oliver.fancysitula.versions.v1_21_11.packets.ClientboundClearDialogPacketImpl();
            }
            case v1_21_9, v1_21_10 -> {
                return new de.oliver.fancysitula.versions.v1_21_9.packets.ClientboundClearDialogPacketImpl();
            }
            case v1_21_6, v1_21_7, v1_21_8 -> {
                return new de.oliver.fancysitula.versions.v1_21_6.packets.ClientboundClearDialogPacketImpl();
            }
            default ->
                    throw new IllegalArgumentException("Unsupported server version: " + ServerVersion.getCurrentVersion());
        }
    }
}
