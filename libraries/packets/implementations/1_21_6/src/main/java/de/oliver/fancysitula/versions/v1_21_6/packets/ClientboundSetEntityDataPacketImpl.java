package de.oliver.fancysitula.versions.v1_21_6.packets;

import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;
import de.oliver.fancysitula.api.utils.reflections.ReflectionUtils;
import de.oliver.fancysitula.versions.v1_21_6.utils.VanillaPlayerAdapter;
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
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.ChickenVariant;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.CowVariant;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.PigVariant;
import net.minecraft.world.entity.animal.frog.FrogVariant;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.monster.SpellcasterIllager;
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

                // Handle Cow variant (data-driven registry - 1.21.5+)
                if (data.getValue() instanceof String s && accessorFieldName.equals("DATA_VARIANT_ID")
                        && entityClassName.contains("Cow") && !entityClassName.contains("Mushroom")) {
                    String variantKey = s.startsWith("minecraft:") ? s : "minecraft:" + s.toLowerCase();
                    ResourceLocation loc = ResourceLocation.parse(variantKey);
                    MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
                    Registry<CowVariant> registry = server.registryAccess().lookupOrThrow(Registries.COW_VARIANT);
                    Holder.Reference<CowVariant> holder = registry.get(loc).orElse(null);
                    if (holder != null) {
                        vanillaValue = holder;
                    }
                }

                // Handle Chicken variant (data-driven registry - 1.21.5+)
                if (data.getValue() instanceof String s && accessorFieldName.equals("DATA_VARIANT_ID")
                        && entityClassName.contains("Chicken")) {
                    String variantKey = s.startsWith("minecraft:") ? s : "minecraft:" + s.toLowerCase();
                    ResourceLocation loc = ResourceLocation.parse(variantKey);
                    MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
                    Registry<ChickenVariant> registry = server.registryAccess().lookupOrThrow(Registries.CHICKEN_VARIANT);
                    Holder.Reference<ChickenVariant> holder = registry.get(loc).orElse(null);
                    if (holder != null) {
                        vanillaValue = holder;
                    }
                }

                // Handle Pig variant (data-driven registry - 1.21.5+)
                if (data.getValue() instanceof String s && accessorFieldName.equals("DATA_VARIANT_ID")
                        && entityClassName.contains("Pig") && !entityClassName.contains("Piglin")) {
                    String variantKey = s.startsWith("minecraft:") ? s : "minecraft:" + s.toLowerCase();
                    ResourceLocation loc = ResourceLocation.parse(variantKey);
                    MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
                    Registry<PigVariant> registry = server.registryAccess().lookupOrThrow(Registries.PIG_VARIANT);
                    Holder.Reference<PigVariant> holder = registry.get(loc).orElse(null);
                    if (holder != null) {
                        vanillaValue = holder;
                    }
                }

                // Handle Sniffer state enum
                if (data.getValue() instanceof String s && accessorFieldName.equals("DATA_STATE")
                        && entityClassName.contains("Sniffer")) {
                    try {
                        vanillaValue = Sniffer.State.valueOf(s.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        vanillaValue = Sniffer.State.IDLING;
                    }
                }

                // Handle Mooshroom type enum (RED/BROWN)
                if (data.getValue() instanceof String s && accessorFieldName.equals("DATA_TYPE")
                        && entityClassName.contains("MushroomCow")) {
                    try {
                        vanillaValue = MushroomCow.Variant.valueOf(s.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        vanillaValue = MushroomCow.Variant.RED;
                    }
                }

                // Handle Spellcaster spell enum (passed as Byte ID)
                if (data.getValue() instanceof Byte b && accessorFieldName.equals("DATA_SPELL_CASTING_ID")
                        && entityClassName.contains("SpellcasterIllager")) {
                    // The spell is stored as Byte in the entity data, so no conversion needed
                    // Just ensure it's a valid byte value
                    vanillaValue = b;
                }

                // Handle VillagerData - supports format: "type:X,profession:Y,level:Z" (all optional)
                // Also supports legacy format: "profession:X" or "type:X" alone
                if (data.getValue() instanceof String s && accessorFieldName.equals("DATA_VILLAGER_DATA")
                        && entityClassName.contains("Villager")) {
                    String typeKey = "minecraft:plains";
                    String profKey = "minecraft:none";
                    int level = 1;

                    // Parse comma-separated values
                    for (String part : s.split(",")) {
                        part = part.trim();
                        if (part.startsWith("type:")) {
                            typeKey = part.substring("type:".length());
                        } else if (part.startsWith("profession:")) {
                            profKey = part.substring("profession:".length());
                        } else if (part.startsWith("level:")) {
                            try {
                                level = Integer.parseInt(part.substring("level:".length()));
                            } catch (NumberFormatException ignored) {}
                        }
                    }

                    ResourceLocation typeLoc = ResourceLocation.parse(typeKey);
                    ResourceLocation profLoc = ResourceLocation.parse(profKey);
                    Holder.Reference<VillagerType> typeHolder = BuiltInRegistries.VILLAGER_TYPE.get(typeLoc).orElse(null);
                    Holder.Reference<VillagerProfession> profHolder = BuiltInRegistries.VILLAGER_PROFESSION.get(profLoc).orElse(null);

                    if (typeHolder != null && profHolder != null) {
                        vanillaValue = new VillagerData(typeHolder, profHolder, level);
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
