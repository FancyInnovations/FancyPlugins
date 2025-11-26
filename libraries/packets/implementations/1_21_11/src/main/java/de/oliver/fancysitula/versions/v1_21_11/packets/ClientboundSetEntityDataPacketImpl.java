package de.oliver.fancysitula.versions.v1_21_11.packets;

import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;
import de.oliver.fancysitula.api.utils.reflections.ReflectionUtils;
import de.oliver.fancysitula.versions.v1_21_11.utils.VanillaPlayerAdapter;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ClientboundSetEntityDataPacketImpl extends FS_ClientboundSetEntityDataPacket {

    public ClientboundSetEntityDataPacketImpl(int entityId, List<EntityData> entityData) {
        super(entityId, entityData);
    }

    @Override
    public Object createPacket() {
        List<SynchedEntityData.DataValue<?>> dataValues = new ArrayList<>();
        for (EntityData data : entityData) {
            try {
                Class<?> entityClass = Class.forName(data.getAccessor().entityClassName());
                net.minecraft.network.syncher.EntityDataAccessor<Object> accessor = ReflectionUtils.getStaticField(entityClass, data.getAccessor().accessorFieldName());

                Object vanillaValue = data.getValue();

                if (data.getValue() == null) {
                    continue;
                }

                if (data.getValue() instanceof Component c) {
                    vanillaValue = PaperAdventure.asVanilla(c);
                }

                if (data.getValue() instanceof ItemStack i) {
                    vanillaValue = net.minecraft.world.item.ItemStack.fromBukkitCopy(i);
                }

                if (data.getValue() instanceof BlockState b) {
                    vanillaValue = ((CraftBlockState) b).getHandle();
                }

                dataValues.add(SynchedEntityData.DataValue.create(accessor, vanillaValue));
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return new ClientboundSetEntityDataPacket(entityId, dataValues);
    }

    @Override
    public void sendPacketTo(FS_RealPlayer player) {
        ClientboundSetEntityDataPacket packet = (ClientboundSetEntityDataPacket) createPacket();

        ServerPlayer vanillaPlayer = VanillaPlayerAdapter.asVanilla(player.getBukkitPlayer());
        vanillaPlayer.connection.send(packet);
    }
}
