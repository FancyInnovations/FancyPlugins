package de.oliver.fancysitula.versions.v1_21_9.packets;

import de.oliver.fancysitula.api.utils.AngelConverter;
import de.oliver.fancysitula.versions.v1_21_11.packets.ClientboundAddEntityPacketImpl;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import org.bukkit.entity.EntityType;

import java.util.UUID;

class ClientboundAddEntityPacketImplTest {

    //TODO: Fix this test (registry problems)
    //    @Test
    void createPacket() {
        int entityId = 10000;
        UUID entityUUID = UUID.randomUUID();
        EntityType entityType = EntityType.PIG;

        double x = 5;
        double y = 57;
        double z = 203;

        float yaw = 142;
        float pitch = 247;
        float headYaw = 90;

        int velocityX = 0;
        int velocityY = 0;
        int velocityZ = 0;

        int data = 0;

        ClientboundAddEntityPacketImpl packet = new ClientboundAddEntityPacketImpl(
                entityId,
                entityUUID,
                entityType,
                x,
                y,
                z,
                yaw,
                pitch,
                headYaw,
                velocityX,
                velocityY,
                velocityZ,
                data
        );

        ClientboundAddEntityPacket createdPacket = (ClientboundAddEntityPacket) packet.createPacket();

        assert createdPacket.getId() == entityId;
        assert createdPacket.getUUID().equals(entityUUID);
        assert createdPacket.getType().getDescriptionId().equals(entityType.getKey().getKey());
        assert createdPacket.getX() == x;
        assert createdPacket.getY() == y;
        assert createdPacket.getZ() == z;
        assert createdPacket.getYRot() == AngelConverter.degreesToVanillaByte(yaw);
        assert createdPacket.getXRot() == AngelConverter.degreesToVanillaByte(pitch);
        assert createdPacket.getYHeadRot() == AngelConverter.degreesToVanillaByte(headYaw);
        assert createdPacket.getMovement().x == velocityX;
        assert createdPacket.getMovement().y == velocityY;
        assert createdPacket.getMovement().z == velocityZ;
        assert createdPacket.getData() == data;
    }
}