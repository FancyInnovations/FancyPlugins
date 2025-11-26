package de.oliver.fancysitula.versions.v1_21_11.packets;

import com.mojang.datafixers.util.Pair;
import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.packets.FS_ClientboundSetEquipmentPacket;
import de.oliver.fancysitula.api.utils.FS_EquipmentSlot;
import de.oliver.fancysitula.versions.v1_21_11.utils.VanillaPlayerAdapter;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientboundSetEquipmentPacketImpl extends FS_ClientboundSetEquipmentPacket {

    public ClientboundSetEquipmentPacketImpl(int entityId, Map<FS_EquipmentSlot, ItemStack> equipment) {
        super(entityId, equipment);
    }

    @Override
    public Object createPacket() {
        List<Pair<net.minecraft.world.entity.EquipmentSlot, net.minecraft.world.item.ItemStack>> slots = new ArrayList<>();

        for (Map.Entry<FS_EquipmentSlot, ItemStack> entry : equipment.entrySet()) {
            EquipmentSlot equipmentSlot = net.minecraft.world.entity.EquipmentSlot.byName(entry.getKey().name().toLowerCase());
            net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(entry.getValue());

            slots.add(Pair.of(equipmentSlot, itemStack));
        }

        return new ClientboundSetEquipmentPacket(entityId, slots);
    }

    @Override
    public void sendPacketTo(FS_RealPlayer player) {
        ClientboundSetEquipmentPacket packet = (ClientboundSetEquipmentPacket) createPacket();

        ServerPlayer vanillaPlayer = VanillaPlayerAdapter.asVanilla(player.getBukkitPlayer());
        vanillaPlayer.connection.send(packet);
    }
}
