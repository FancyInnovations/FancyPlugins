package de.oliver.fancysitula.versions.v1_21_6.packets;

import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.packets.FS_ClientboundTeleportEntityPacket;
import de.oliver.fancysitula.versions.v1_21_6.utils.VanillaPlayerAdapter;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;

public class ClientboundTeleportEntityPacketImpl extends FS_ClientboundTeleportEntityPacket {

    public ClientboundTeleportEntityPacketImpl(int entityId, double x, double y, double z, float yaw, float pitch, boolean onGround) {
        super(entityId, x, y, z, yaw, pitch, onGround);
    }

    @Override
    public Object createPacket() {
        // Since 1.21.2 ClientboundTeleportEntityPacket is "Synchronize Vehicle Position"; the regular entity teleport is ClientboundEntityPositionSyncPacket
        ClientboundEntityPositionSyncPacket packet = new ClientboundEntityPositionSyncPacket(
                entityId,
                new PositionMoveRotation(
                        new Vec3(x, y, z),
                        Vec3.ZERO,
                        yaw,
                        pitch
                ),
                onGround
        );

        return packet;
    }

    @Override
    public void sendPacketTo(FS_RealPlayer player) {
        ClientboundEntityPositionSyncPacket packet = (ClientboundEntityPositionSyncPacket) createPacket();

        ServerPlayer vanillaPlayer = VanillaPlayerAdapter.asVanilla(player.getBukkitPlayer());
        vanillaPlayer.connection.send(packet);
    }
}
