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
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.FrogVariant;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class FrogAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "variant",
                getFrogVariantRegistry()
                        .listElementIds()
                        .map(id -> id.identifier().getPath())
                        .toList(),
                List.of(EntityType.FROG),
                FrogAttributes::setVariant
        ));

        return attributes;
    }

    private static void setVariant(Npc npc, String value) {
        final Frog frog = ReflectionHelper.getEntity(npc);

        Holder<FrogVariant> variant = getFrogVariantRegistry()
                .get(ResourceKey.create(
                        Registries.FROG_VARIANT,
                        Identifier.withDefaultNamespace(value.toLowerCase())
                ))
                .orElseThrow();

        frog.setVariant(variant);
    }

    private static HolderLookup.RegistryLookup<FrogVariant> getFrogVariantRegistry() {
        return MinecraftServer.getServer().registryAccess().lookupOrThrow(Registries.FROG_VARIANT);
    }
}
