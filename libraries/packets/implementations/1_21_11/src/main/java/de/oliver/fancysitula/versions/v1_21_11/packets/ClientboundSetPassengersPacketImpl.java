package de.oliver.fancysitula.versions.v1_21_11.packets;

import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.packets.FS_ClientboundSetPassengersPacket;
import de.oliver.fancysitula.api.utils.reflections.ReflectionUtils;
import de.oliver.fancysitula.versions.v1_21_11.utils.VanillaPlayerAdapter;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class ClientboundSetPassengersPacketImpl extends FS_ClientboundSetPassengersPacket {

    public ClientboundSetPassengersPacketImpl(int entityId, List<Integer> passengers) {
        super(entityId, passengers);
    }


    @Override
    public Object createPacket() {
        int[] passengers = new int[this.passengers.size()];
        for (int i = 0; i < this.passengers.size(); i++) {
            passengers[i] = this.passengers.get(i);
        }

        try {
            ClientboundSetPassengersPacket packet = ReflectionUtils.createUnsafeInstance(ClientboundSetPassengersPacket.class);
            ReflectionUtils.setFinalField(packet, "vehicle", entityId);
            ReflectionUtils.setFinalField(packet, "passengers", passengers);
            return packet;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void sendPacketTo(FS_RealPlayer player) {
        ClientboundSetPassengersPacket packet = (ClientboundSetPassengersPacket) createPacket();

        ServerPlayer vanillaPlayer = VanillaPlayerAdapter.asVanilla(player.getBukkitPlayer());
        vanillaPlayer.connection.send(packet);
    }
}
