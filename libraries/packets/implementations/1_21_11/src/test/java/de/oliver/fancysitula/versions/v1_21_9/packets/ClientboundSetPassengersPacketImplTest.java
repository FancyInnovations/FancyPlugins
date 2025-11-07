package de.oliver.fancysitula.versions.v1_21_9.packets;

import de.oliver.fancysitula.versions.v1_21_11.packets.ClientboundSetPassengersPacketImpl;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ClientboundSetPassengersPacketImplTest {

    @Test
    void createPacket() {
        // Setup packet
        int vehicleID = 712;
        List<Integer> passengers = new ArrayList<>();
        passengers.add(571);
        passengers.add(572);

        ClientboundSetPassengersPacketImpl packet = new ClientboundSetPassengersPacketImpl(vehicleID, passengers);
        ClientboundSetPassengersPacket createdPacket = (ClientboundSetPassengersPacket) packet.createPacket();

        // Check packet
        assert createdPacket.getVehicle() == vehicleID;
        assert createdPacket.getPassengers().length == 2;
        assert createdPacket.getPassengers()[0] == 571;
        assert createdPacket.getPassengers()[1] == 572;
    }
}
