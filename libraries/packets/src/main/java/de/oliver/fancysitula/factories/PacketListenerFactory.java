package de.oliver.fancysitula.factories;

import de.oliver.fancysitula.api.packets.FS_ServerboundPacket;
import de.oliver.fancysitula.api.utils.FS_PacketListener;
import de.oliver.fancysitula.api.utils.ServerVersion;

public class PacketListenerFactory {

    public FS_PacketListener createPacketListener(FS_ServerboundPacket.Type packet) {
        switch (ServerVersion.getCurrentVersion()) {
            case v1_21_11 -> {
                return new de.oliver.fancysitula.versions.v1_21_11.utils.PacketListenerImpl(packet);
            }
            case v1_21_9, v1_21_10 -> {
                return new de.oliver.fancysitula.versions.v1_21_9.utils.PacketListenerImpl(packet);
            }
            case v1_21_6, v1_21_7, v1_21_8 -> {
                return new de.oliver.fancysitula.versions.v1_21_6.utils.PacketListenerImpl(packet);
            }
            default ->
                    throw new IllegalArgumentException("Unsupported server version: " + ServerVersion.getCurrentVersion());
        }
    }

}
