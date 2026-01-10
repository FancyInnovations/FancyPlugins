package de.oliver.fancysitula.api.packets;

import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * A packet that bundles multiple packets together and sends them atomically.
 * This is important for PLAYER type NPCs where PlayerInfoUpdate must be received
 * by the client before AddEntity, otherwise the client cannot properly render the player skin.
 */
public abstract class FS_ClientboundBundlePacket extends FS_ClientboundPacket {

    protected final List<FS_ClientboundPacket> packets;

    public FS_ClientboundBundlePacket(List<FS_ClientboundPacket> packets) {
        this.packets = new ArrayList<>(packets);
    }

    public List<FS_ClientboundPacket> getPackets() {
        return packets;
    }

    /**
     * Creates raw NMS packets from all bundled packets
     */
    @ApiStatus.Internal
    protected List<Object> createRawPackets() {
        List<Object> rawPackets = new ArrayList<>();
        for (FS_ClientboundPacket packet : packets) {
            Object raw = packet.createPacket();
            if (raw != null) {
                rawPackets.add(raw);
            }
        }
        return rawPackets;
    }
}
