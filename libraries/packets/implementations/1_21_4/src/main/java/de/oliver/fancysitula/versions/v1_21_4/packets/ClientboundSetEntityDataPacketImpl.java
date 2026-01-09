package de.oliver.fancysitula.versions.v1_21_4.packets;

import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;
import de.oliver.fancysitula.api.utils.reflections.ReflectionUtils;
import de.oliver.fancysitula.versions.v1_21_4.utils.VanillaPlayerAdapter;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.WolfVariant;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        // Use the server's class loader to avoid Paper's reflection remapper issues
        ClassLoader classLoader = MinecraftServer.class.getClassLoader();
        Class<?> resolved = classLoader.loadClass(className);
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

                // Handle Optional<Component> for custom names
                if (data.getValue() instanceof Optional<?> opt) {
                    if (opt.isPresent() && opt.get() instanceof Component c) {
                        vanillaValue = Optional.of(PaperAdventure.asVanilla(c));
                    } else {
                        vanillaValue = Optional.empty();
                    }
                }

                if (data.getValue() instanceof ItemStack i) {
                    vanillaValue = net.minecraft.world.item.ItemStack.fromBukkitCopy(i);
                }

                if (data.getValue() instanceof BlockState b) {
                    vanillaValue = ((CraftBlockState) b).getHandle();
                }

                // Handle Armadillo state enum conversion (value passed as String)
                if (data.getValue() instanceof String s && accessorFieldName.equals("DATA_STATE")
                        && entityClassName.contains("Armadillo")) {
                    try {
                        vanillaValue = Armadillo.ArmadilloState.valueOf(s.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        vanillaValue = Armadillo.ArmadilloState.IDLE;
                    }
                }

                // Handle Wolf variant (data-driven registry)
                if (data.getValue() instanceof String s && accessorFieldName.equals("DATA_VARIANT_ID")
                        && entityClassName.contains("Wolf")) {
                    String variantKey = s.startsWith("wolf:") ? s.substring("wolf:".length()) : s;
                    ResourceLocation loc = ResourceLocation.parse(variantKey);
                    MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
                    Registry<WolfVariant> registry = server.registryAccess().lookupOrThrow(Registries.WOLF_VARIANT);
                    Holder.Reference<WolfVariant> holder = registry.get(loc).orElse(null);
                    if (holder != null) {
                        vanillaValue = holder;
                    }
                }

                // Handle Cat variant (data-driven registry)
                if (data.getValue() instanceof String s && accessorFieldName.equals("DATA_VARIANT_ID")
                        && entityClassName.contains("Cat")) {
                    String variantKey = s.startsWith("cat:") ? s.substring("cat:".length()) : s;
                    ResourceLocation loc = ResourceLocation.parse(variantKey);
                    MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
                    Registry<CatVariant> registry = server.registryAccess().lookupOrThrow(Registries.CAT_VARIANT);
                    Holder.Reference<CatVariant> holder = registry.get(loc).orElse(null);
                    if (holder != null) {
                        vanillaValue = holder;
                    }
                }

                // Handle Frog variant (data-driven registry)
                if (data.getValue() instanceof String s && accessorFieldName.equals("DATA_VARIANT_ID")
                        && entityClassName.contains("Frog")) {
                    String variantKey = s.startsWith("frog:") ? s.substring("frog:".length()) : s;
                    ResourceLocation loc = ResourceLocation.parse(variantKey);
                    MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
                    Registry<FrogVariant> registry = server.registryAccess().lookupOrThrow(Registries.FROG_VARIANT);
                    Holder.Reference<FrogVariant> holder = registry.get(loc).orElse(null);
                    if (holder != null) {
                        vanillaValue = holder;
                    }
                }

                // Handle VillagerData (profession, type, or combined format)
                if (data.getValue() instanceof String s && accessorFieldName.equals("DATA_VILLAGER_DATA")
                        && entityClassName.contains("Villager")) {
                    // Combined format: "type:minecraft:plains,profession:minecraft:farmer,level:3" (all optional)
                    // Legacy formats: "profession:minecraft:farmer" or "type:minecraft:plains"
                    if (s.contains(",") || (s.contains("type:") && s.contains("profession:"))) {
                        // Combined format - parse all components
                        VillagerType villagerType = VillagerType.PLAINS;
                        VillagerProfession profession = VillagerProfession.NONE;
                        int level = 1;

                        String[] parts = s.split(",");
                        for (String part : parts) {
                            part = part.trim();
                            if (part.startsWith("type:")) {
                                String typeKey = part.substring("type:".length());
                                ResourceLocation loc = ResourceLocation.parse(typeKey);
                                Holder.Reference<VillagerType> holder = BuiltInRegistries.VILLAGER_TYPE.get(loc).orElse(null);
                                if (holder != null) {
                                    villagerType = holder.value();
                                }
                            } else if (part.startsWith("profession:")) {
                                String profKey = part.substring("profession:".length());
                                ResourceLocation loc = ResourceLocation.parse(profKey);
                                Holder.Reference<VillagerProfession> holder = BuiltInRegistries.VILLAGER_PROFESSION.get(loc).orElse(null);
                                if (holder != null) {
                                    profession = holder.value();
                                }
                            } else if (part.startsWith("level:")) {
                                try {
                                    level = Integer.parseInt(part.substring("level:".length()));
                                    level = Math.max(1, Math.min(5, level)); // Clamp to valid range
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        }
                        vanillaValue = new VillagerData(villagerType, profession, level);
                    } else if (s.startsWith("profession:")) {
                        // Legacy format: "profession:minecraft:farmer"
                        String profKey = s.substring("profession:".length());
                        ResourceLocation loc = ResourceLocation.parse(profKey);
                        Holder.Reference<VillagerProfession> holder = BuiltInRegistries.VILLAGER_PROFESSION.get(loc).orElse(null);
                        if (holder != null) {
                            vanillaValue = new VillagerData(VillagerType.PLAINS, holder.value(), 1);
                        }
                    } else if (s.startsWith("type:")) {
                        // Legacy format: "type:minecraft:plains"
                        String typeKey = s.substring("type:".length());
                        ResourceLocation loc = ResourceLocation.parse(typeKey);
                        Holder.Reference<VillagerType> holder = BuiltInRegistries.VILLAGER_TYPE.get(loc).orElse(null);
                        if (holder != null) {
                            vanillaValue = new VillagerData(holder.value(), VillagerProfession.NONE, 1);
                        }
                    }
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
