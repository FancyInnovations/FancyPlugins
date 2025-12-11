package de.oliver.fancysitula.versions.v1_21_9.packets;

import de.oliver.fancysitula.versions.v1_21_11.packets.ClientboundRemoveEntitiesPacketImpl;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import org.junit.jupiter.api.Test;

import java.util.List;

class ClientboundRemoveEntitiesPacketImplTest {

    @Test
    void createPacket() {
        List<Integer> entityIds = List.of(95, 120, 154, 187);

        ClientboundRemoveEntitiesPacketImpl packet = new ClientboundRemoveEntitiesPacketImpl(entityIds);
        ClientboundRemoveEntitiesPacket createdPacket = (ClientboundRemoveEntitiesPacket) packet.createPacket();

        assert createdPacket.getEntityIds().size() == entityIds.size();
        assert createdPacket.getEntityIds().containsAll(entityIds);
    }
}