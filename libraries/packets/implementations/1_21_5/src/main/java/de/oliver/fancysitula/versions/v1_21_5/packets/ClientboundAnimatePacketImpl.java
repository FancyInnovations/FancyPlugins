package de.oliver.fancysitula.versions.v1_21_5.packets;

import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.packets.FS_ClientboundAnimatePacket;
import de.oliver.fancysitula.api.utils.reflections.ReflectionUtils;
import de.oliver.fancysitula.versions.v1_21_5.utils.VanillaPlayerAdapter;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.server.level.ServerPlayer;

public class ClientboundAnimatePacketImpl extends FS_ClientboundAnimatePacket {

    public ClientboundAnimatePacketImpl(int entityId, int animationId) {
        super(entityId, animationId);
    }

    @Override
    public Object createPacket() {
        ClientboundAnimatePacket packet = null;

        try {
            packet = ReflectionUtils.createUnsafeInstance(ClientboundAnimatePacket.class);
            ReflectionUtils.setFinalField(packet, "id", entityId);
            ReflectionUtils.setFinalField(packet, "action", animationId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return packet;
    }

    @Override
    public void sendPacketTo(FS_RealPlayer player) {
        ClientboundAnimatePacket packet = (ClientboundAnimatePacket) createPacket();
        ServerPlayer vanillaPlayer = VanillaPlayerAdapter.asVanilla(player.getBukkitPlayer());
        vanillaPlayer.connection.send(packet);
    }
}
