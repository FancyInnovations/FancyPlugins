package de.oliver.fancysitula.versions.v1_21_9.packets;

import de.oliver.fancysitula.api.utils.reflections.ReflectionUtils;
import de.oliver.fancysitula.versions.v1_21_11.packets.ClientboundRotateHeadPacketImpl;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import org.junit.jupiter.api.Test;

class ClientboundRotateHeadPacketImplTest {

    @Test
    void createPacket() throws Exception {
        int entityId = 184;
        float headYaw = 45;

        ClientboundRotateHeadPacketImpl packet = new ClientboundRotateHeadPacketImpl(entityId, (byte) headYaw);
        ClientboundRotateHeadPacket createdPacket = (ClientboundRotateHeadPacket) packet.createPacket();

        assert ReflectionUtils.getField(createdPacket, "entityId").equals(entityId);
        assert createdPacket.getYHeadRot() == headYaw;
    }
}