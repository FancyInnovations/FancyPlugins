package de.oliver.fancysitula.versions.v26_2.packets;

import org.junit.jupiter.api.Test;

public class ClientboundClearDialogPacketImplTest {

    @Test
    void createPacket() {
        ClientboundClearDialogPacketImpl packet = new ClientboundClearDialogPacketImpl();
        assert packet.createPacket() != null : "Packet creation failed";
//         assert packet.equals(ClientboundClearDialogPacket.INSTANCE);
    }

}
