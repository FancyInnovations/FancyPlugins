package de.oliver.fancysitula.versions.v1_20_6.packets;

import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;
import de.oliver.fancysitula.api.utils.reflections.ReflectionUtils;
import de.oliver.fancysitula.versions.v1_20_6.utils.VanillaPlayerAdapter;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClientboundSetEntityDataPacketImpl extends FS_ClientboundSetEntityDataPacket {

    private static final ConcurrentMap<String, Class<?>> ENTITY_CLASS_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, net.minecraft.network.syncher.EntityDataAccessor<Object>> ENTITY_ACCESSOR_CACHE = new ConcurrentHashMap<>();

    public ClientboundSetEntityDataPacketImpl(int entityId, List<EntityData> entityData) {
        super(entityId, entityData);
    }

    private static Class<?> getEntityClassCached(String className) throws ClassNotFoundException {
        Class<?> cached = ENTITY_CLASS_CACHE.get(className);
        if (cached != null) {
            return cached;
        }
        Class<?> resolved = Class.forName(className);
        ENTITY_CLASS_CACHE.put(className, resolved);
        return resolved;
    }

    private static net.minecraft.network.syncher.EntityDataAccessor<Object> getAccessorCached(String entityClassName, String fieldName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        String key = entityClassName + "#" + fieldName;
        net.minecraft.network.syncher.EntityDataAccessor<Object> cached = ENTITY_ACCESSOR_CACHE.get(key);
        if (cached != null) {
            return cached;
        }
        Class<?> entityClass = getEntityClassCached(entityClassName);
        net.minecraft.network.syncher.EntityDataAccessor<Object> accessor = ReflectionUtils.getStaticField(entityClass, fieldName);
        ENTITY_ACCESSOR_CACHE.put(key, accessor);
        return accessor;
    }

    @Override
    public Object createPacket() {
        List<SynchedEntityData.DataValue<?>> dataValues = new ArrayList<>();
        for (EntityData data : entityData) {
            try {
                String entityClassName = data.getAccessor().entityClassName();
                String accessorFieldName = data.getAccessor().accessorFieldName();
                net.minecraft.network.syncher.EntityDataAccessor<Object> accessor = getAccessorCached(entityClassName, accessorFieldName);

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
