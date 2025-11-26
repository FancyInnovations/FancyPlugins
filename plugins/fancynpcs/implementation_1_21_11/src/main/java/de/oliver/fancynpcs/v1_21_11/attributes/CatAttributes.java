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
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.CatVariant;
import net.minecraft.world.item.DyeColor;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class CatAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "variant",
                getCatVariantRegistry()
                        .listElementIds()
                        .map(id -> id.identifier().getPath())
                        .toList(),
                List.of(EntityType.CAT),
                CatAttributes::setVariant
        ));

        attributes.add(new NpcAttribute(
                "pose",
                List.of("standing", "sleeping", "sitting"),
                List.of(EntityType.CAT),
                CatAttributes::setPose
        ));

        attributes.add(new NpcAttribute(
                "collar_color",
                List.of("RED", "BLUE", "YELLOW", "GREEN", "PURPLE", "ORANGE", "LIME", "MAGENTA", "BROWN", "WHITE", "GRAY", "LIGHT_GRAY", "LIGHT_BLUE", "BLACK", "CYAN", "PINK", "NONE"),
                List.of(EntityType.CAT),
                CatAttributes::setCollarColor
        ));

        return attributes;
    }

    private static void setVariant(Npc npc, String value) {
        final Cat cat = ReflectionHelper.getEntity(npc);

        Holder<CatVariant> variant = getCatVariantRegistry()
                .get(ResourceKey.create(
                        Registries.CAT_VARIANT,
                        Identifier.withDefaultNamespace(value.toLowerCase())
                ))
                .orElseThrow();

        cat.setVariant(variant);
    }

    private static void setPose(Npc npc, String value) {
        final Cat cat = ReflectionHelper.getEntity(npc);
        switch (value.toLowerCase()) {
            case "standing" -> {
                cat.setInSittingPose(false, false);
                cat.setLying(false);
            }
            case "sleeping" -> {
                cat.setInSittingPose(false, false);
                cat.setLying(true);
            }
            case "sitting" -> {
                cat.setLying(false);
                cat.setOrderedToSit(true);
                cat.setInSittingPose(true, false);
            }
        }
    }

    private static HolderLookup.RegistryLookup<CatVariant> getCatVariantRegistry() {
        return MinecraftServer.getServer().registryAccess().lookupOrThrow(Registries.CAT_VARIANT);
    }

    private static void setCollarColor(Npc npc, String value) {
        Cat cat = ReflectionHelper.getEntity(npc);

        if (value.equalsIgnoreCase("none") || value.isEmpty()) {
            // Reset to no collar
            cat.setTame(false, false);
            return;
        }

        try {
            DyeColor color = DyeColor.valueOf(value.toUpperCase());
            if (!cat.isTame()) {
                cat.setTame(true, false);
            }
            cat.setCollarColor(color);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid cat collar color: " + value);
        }
    }
}
