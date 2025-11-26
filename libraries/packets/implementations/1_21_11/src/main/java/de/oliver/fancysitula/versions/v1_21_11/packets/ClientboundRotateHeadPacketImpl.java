package de.oliver.fancysitula.versions.v1_21_11.packets;

import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.packets.FS_ClientboundRotateHeadPacket;
import de.oliver.fancysitula.api.utils.AngelConverter;
import de.oliver.fancysitula.api.utils.reflections.ReflectionUtils;
import de.oliver.fancysitula.versions.v1_21_11.utils.VanillaPlayerAdapter;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.server.level.ServerPlayer;

public class ClientboundRotateHeadPacketImpl extends FS_ClientboundRotateHeadPacket {

    public ClientboundRotateHeadPacketImpl(int entityId, float headYaw) {
        super(entityId, headYaw);
    }

    @Override
    public Object createPacket() {
        ClientboundRotateHeadPacket packet = null;

        try {
            packet = ReflectionUtils.createUnsafeInstance(ClientboundRotateHeadPacket.class);
            ReflectionUtils.setFinalField(packet, "entityId", entityId);
            ReflectionUtils.setFinalField(packet, "yHeadRot", AngelConverter.degreesToVanillaByte(headYaw));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return packet;
    }

    @Override
    public void sendPacketTo(FS_RealPlayer player) {
        ClientboundRotateHeadPacket packet = (ClientboundRotateHeadPacket) createPacket();

        ServerPlayer vanillaPlayer = VanillaPlayerAdapter.asVanilla(player.getBukkitPlayer());
        vanillaPlayer.connection.send(packet);
    }
}
