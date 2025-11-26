package de.oliver.fancysitula.versions.v1_21_11.packets;

import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.packets.FS_ClientboundClearDialogPacket;
import de.oliver.fancysitula.versions.v1_21_11.utils.VanillaPlayerAdapter;
import net.minecraft.network.protocol.common.ClientboundClearDialogPacket;
import net.minecraft.server.level.ServerPlayer;

public class ClientboundClearDialogPacketImpl extends FS_ClientboundClearDialogPacket {
    @Override
    public Object createPacket() {
        return ClientboundClearDialogPacket.INSTANCE;
    }

    @Override
    protected void sendPacketTo(FS_RealPlayer player) {
        ClientboundClearDialogPacket packet = (ClientboundClearDialogPacket) createPacket();

        ServerPlayer vanillaPlayer = VanillaPlayerAdapter.asVanilla(player.getBukkitPlayer());
        vanillaPlayer.connection.send(packet);
    }
}
