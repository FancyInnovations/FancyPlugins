package de.oliver.fancysitula.versions.v1_21_9.packets;

import de.oliver.fancysitula.api.utils.FS_EquipmentSlot;
import de.oliver.fancysitula.versions.v1_21_11.packets.ClientboundSetEquipmentPacketImpl;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

class ClientboundSetEquipmentPacketImplTest {

    //TODO: Fix this test (registry problems)
    //    @Test
    void createPacket() {
        // Setup packet
        Map<FS_EquipmentSlot, ItemStack> equipment = Map.of(
                FS_EquipmentSlot.MAINHAND, new ItemStack(Material.DIAMOND_SWORD),
                FS_EquipmentSlot.OFFHAND, new ItemStack(Material.SHIELD),
                FS_EquipmentSlot.HEAD, new ItemStack(Material.DIAMOND_HELMET)
        );

        ClientboundSetEquipmentPacketImpl packet = new ClientboundSetEquipmentPacketImpl(42, equipment);
        ClientboundSetEquipmentPacket createdPacket = (ClientboundSetEquipmentPacket) packet.createPacket();

        assert createdPacket.getEntity() == 42;
        assert createdPacket.getSlots().size() == 3;
    }
}