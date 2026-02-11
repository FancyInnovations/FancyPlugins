package de.oliver.fancynpcs.v1_21_11;

import de.oliver.fancylib.serverSoftware.ServerSoftware;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;

public class PacketHelper {

    /**
     * Sends a packet to a player, taking into account Folia's threading model.
     *
     * @param packet The packet to send.
     * @param player The player to send the packet to.
     */
    public static void sendPacketToPlayer(Packet<?> packet, ServerPlayer player) {
        if (ServerSoftware.isFolia()) {
            player.getBukkitEntity().getScheduler().run(
                    FancyNpcsPlugin.get().getPlugin(),
                    (t) -> player.connection.send(packet),
                    null
            );
            return;
        }

        player.connection.send(packet);
    }

}
