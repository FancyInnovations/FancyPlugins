package de.oliver.fancysitula.versions.v26_2.packets;

import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import org.junit.jupiter.api.Test;

class ClientboundTeleportEntityPacketImplTest {

    @Test
    void createPacket() {
        int entityId = 4313;
        double x = 15.0;
        double y = 57.0;
        double z = -27.0;
        float yaw = 90.0f;
        float pitch = 45.0f;
        boolean onGround = true;

        ClientboundTeleportEntityPacketImpl packet = new ClientboundTeleportEntityPacketImpl(entityId, x, y, z, yaw, pitch, onGround);
        ClientboundEntityPositionSyncPacket createdPacket = (ClientboundEntityPositionSyncPacket) packet.createPacket();

        assert createdPacket != null;
        assert createdPacket.id() == entityId;
        assert createdPacket.position().endPosition().x == x;
        assert createdPacket.position().endPosition().y == y;
        assert createdPacket.position().endPosition().z == z;
        assert createdPacket.xRot() == pitch;
        assert createdPacket.yRot() == yaw;
        assert createdPacket.onGround() == onGround;
    }
}