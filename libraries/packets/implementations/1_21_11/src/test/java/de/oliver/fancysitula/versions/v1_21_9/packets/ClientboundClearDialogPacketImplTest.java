package de.oliver.fancysitula.versions.v1_21_9.packets;

import de.oliver.fancysitula.versions.v1_21_11.packets.ClientboundClearDialogPacketImpl;
import org.junit.jupiter.api.Test;

public class ClientboundClearDialogPacketImplTest {

    @Test
    void createPacket() {
        ClientboundClearDialogPacketImpl packet = new ClientboundClearDialogPacketImpl();
        assert packet.createPacket() != null : "Packet creation failed";
//         assert packet.equals(ClientboundClearDialogPacket.INSTANCE);
    }

}
