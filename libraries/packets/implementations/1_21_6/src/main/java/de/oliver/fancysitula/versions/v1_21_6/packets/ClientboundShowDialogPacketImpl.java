package de.oliver.fancysitula.versions.v1_21_6.packets;

import de.oliver.fancysitula.api.dialogs.FS_Dialog;
import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.packets.FS_ClientboundShowDialogPacket;
import de.oliver.fancysitula.versions.v1_21_6.utils.VanillaPlayerAdapter;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.common.ClientboundShowDialogPacket;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.level.ServerPlayer;

public class ClientboundShowDialogPacketImpl extends FS_ClientboundShowDialogPacket {

    public ClientboundShowDialogPacketImpl(FS_Dialog dialog) {
        super(dialog);
    }

    @Override
    public Object createPacket() {
        Holder<Dialog> holder = Holder.direct(toNms(dialog));
        return new ClientboundShowDialogPacket(holder);
    }

    @Override
    protected void sendPacketTo(FS_RealPlayer player) {
        ClientboundShowDialogPacket packet = (ClientboundShowDialogPacket) createPacket();

        ServerPlayer vanillaPlayer = VanillaPlayerAdapter.asVanilla(player.getBukkitPlayer());
        vanillaPlayer.connection.send(packet);
    }

    private Dialog toNms(FS_Dialog dialog) {
        return null;
    }
}
