package de.oliver.fancynpcs.v1_21_11.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_21_11.ReflectionHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.PigVariant;
import net.minecraft.world.item.Items;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class PigAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "variant",
                getPigVariantRegistry()
                        .listElementIds()
                        .map(id -> id.identifier().getPath())
                        .toList(),
                List.of(EntityType.PIG),
                PigAttributes::setVariant
        ));

        attributes.add(new NpcAttribute(
                "has_saddle",
                List.of("true", "false"),
                List.of(EntityType.PIG),
                PigAttributes::setHasSaddle
        ));

        return attributes;
    }

    private static void setVariant(Npc npc, String value) {
        final Pig pig = ReflectionHelper.getEntity(npc);

        Holder<PigVariant> variant = getPigVariantRegistry()
                .get(ResourceKey.create(
                        Registries.PIG_VARIANT,
                        Identifier.withDefaultNamespace(value.toLowerCase())
                ))
                .orElseThrow();

        pig.setVariant(variant);
    }

    private static void setHasSaddle(Npc npc, String value) {
        Pig pig = ReflectionHelper.getEntity(npc);

        boolean hasSaddle = Boolean.parseBoolean(value.toLowerCase());

        if (hasSaddle) {
            pig.setItemSlot(EquipmentSlot.SADDLE, Items.SADDLE.getDefaultInstance());
        }
    }

    private static HolderLookup.RegistryLookup<PigVariant> getPigVariantRegistry() {
        return MinecraftServer.getServer().registryAccess().lookupOrThrow(Registries.PIG_VARIANT);
    }

}
