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
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.CowVariant;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class CowAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "variant",
                getCowVariantRegistry()
                        .listElementIds()
                        .map(id -> id.identifier().getPath())
                        .toList(),
                List.of(EntityType.COW),
                CowAttributes::setVariant
        ));

        return attributes;
    }

    private static void setVariant(Npc npc, String value) {
        final Cow cow = ReflectionHelper.getEntity(npc);

        Holder<CowVariant> variant = getCowVariantRegistry()
                .get(ResourceKey.create(
                        Registries.COW_VARIANT,
                        Identifier.withDefaultNamespace(value.toLowerCase())
                ))
                .orElseThrow();

        cow.setVariant(variant);
    }

    private static HolderLookup.RegistryLookup<CowVariant> getCowVariantRegistry() {
        return MinecraftServer.getServer().registryAccess().lookupOrThrow(Registries.COW_VARIANT);
    }

}
