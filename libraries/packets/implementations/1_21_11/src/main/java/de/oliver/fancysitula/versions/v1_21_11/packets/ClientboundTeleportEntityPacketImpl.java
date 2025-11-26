package de.oliver.fancysitula.versions.v1_21_11.packets;

import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.packets.FS_ClientboundTeleportEntityPacket;
import de.oliver.fancysitula.versions.v1_21_11.utils.VanillaPlayerAdapter;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class ClientboundTeleportEntityPacketImpl extends FS_ClientboundTeleportEntityPacket {

    public ClientboundTeleportEntityPacketImpl(int entityId, double x, double y, double z, float yaw, float pitch, boolean onGround) {
        super(entityId, x, y, z, yaw, pitch, onGround);
    }

    @Override
    public Object createPacket() {
        ClientboundTeleportEntityPacket packet = new ClientboundTeleportEntityPacket(
                entityId,
                new PositionMoveRotation(
                        new Vec3(x, y, z),
                        Vec3.ZERO,
                        yaw,
                        pitch
                ),
                Set.of(),
                onGround
        );

        return packet;
    }

    @Override
    public void sendPacketTo(FS_RealPlayer player) {
        ClientboundTeleportEntityPacket packet = (ClientboundTeleportEntityPacket) createPacket();

        ServerPlayer vanillaPlayer = VanillaPlayerAdapter.asVanilla(player.getBukkitPlayer());
        vanillaPlayer.connection.send(packet);
    }
}
