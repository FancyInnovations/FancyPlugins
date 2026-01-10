package de.oliver.fancysitula.versions.v1_21_4.packets;

import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.packets.FS_ClientboundBundlePacket;
import de.oliver.fancysitula.api.packets.FS_ClientboundPacket;
import de.oliver.fancysitula.versions.v1_21_4.utils.VanillaPlayerAdapter;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class ClientboundBundlePacketImpl extends FS_ClientboundBundlePacket {

    public ClientboundBundlePacketImpl(List<FS_ClientboundPacket> packets) {
        super(packets);
    }

    @Override
    public Object createPacket() {
        List<Packet<? super ClientGamePacketListener>> rawPackets = new ArrayList<>();
        for (Object raw : createRawPackets()) {
            if (raw instanceof Packet<?>) {
                @SuppressWarnings("unchecked")
                Packet<? super ClientGamePacketListener> packet = (Packet<? super ClientGamePacketListener>) raw;
                rawPackets.add(packet);
            }
        }
        return new ClientboundBundlePacket(rawPackets);
    }

    @Override
    protected void sendPacketTo(FS_RealPlayer player) {
        ClientboundBundlePacket packet = (ClientboundBundlePacket) createPacket();

        ServerPlayer vanillaPlayer = VanillaPlayerAdapter.asVanilla(player.getBukkitPlayer());
        vanillaPlayer.connection.send(packet);
    }
}
