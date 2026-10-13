package com.fancyinnovations.fancyholograms.storage.json;

import com.fancyinnovations.fancyholograms.api.FancyHolograms;
import com.fancyinnovations.fancyholograms.api.data.*;
import com.fancyinnovations.fancyholograms.api.trait.HologramTrait;
import com.fancyinnovations.fancyholograms.api.trait.HologramTraitRegistry;
import com.fancyinnovations.fancyholograms.storage.json.model.*;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3f;

import java.util.Base64;

public class JsonAdapter {

    public static JsonHologramData hologramDataToJson(HologramData data) {
        return new JsonHologramData(
                data.getName(),
                data.getType(),
                new JsonLocation(
                        data.getWorldName(),
                        data.getLocation().getX(),
                        data.getLocation().getY(),
                        data.getLocation().getZ(),
                        data.getLocation().getYaw(),
                        data.getLocation().getPitch()
                ),
                data.getWorldName(),
                data.getVisibilityDistance(),
                data.getVisibility(),
                data.getLinkedNpcName(),
                data.getTraitTrait().getTraits().stream()
                        .map(HologramTrait::getName)
                        .toList()
        );
    }

    public static JsonDisplayHologramData displayHologramDataToJson(DisplayHologramData data) {
        return new JsonDisplayHologramData(
                new JsonVec3f(
                        data.getScale().x(),
                        data.getScale().y(),
                        data.getScale().z()
                ),
                new JsonVec3f(
                        data.getTranslation().x(),
                        data.getTranslation().y(),
                        data.getTranslation().z()
                ),
                data.getShadowRadius(),
                data.getShadowStrength(),
                data.getBrightness() == null ? null : new JsonBrightness(
                        data.getBrightness().getBlockLight(),
                        data.getBrightness().getSkyLight()
                ),
                data.getBillboard(),
                data.getGlowingColor()
        );
    }

    public static JsonTextHologramData textHologramDataToJson(TextHologramData data) {
        return new JsonTextHologramData(
                data.getText(),
                data.hasTextShadow(),
                data.isSeeThrough(),
                data.getTextAlignment(),
                data.getTextUpdateInterval(),
                data.getBackground() == null ? "" : "#" + Integer.toHexString(data.getBackground().asARGB()),
                Byte.toUnsignedInt(data.getTextOpacity())
        );
    }

    public static JsonBlockHologramData blockHologramDataToJson(BlockHologramData data) {
        return new JsonBlockHologramData(
                data.getBlockData() != null ? data.getBlockData().getAsString() : data.getBlock().name()
        );
    }

    public static JsonItemHologramData itemHologramDataToJson(ItemHologramData data) {
        return new JsonItemHologramData(
                Base64.getEncoder().encodeToString(data.getItemStack().serializeAsBytes())
        );
    }

    public static JsonDataUnion toUnion(TextHologramData data) {
        JsonHologramData hologramData = hologramDataToJson(data);
        JsonDisplayHologramData displayHologramData = displayHologramDataToJson(data);
        JsonTextHologramData textHologramData = textHologramDataToJson(data);

        return new JsonDataUnion(
                hologramData,
                displayHologramData,
                textHologramData,
                null,
                null
        );
    }

    public static JsonDataUnion toUnion(ItemHologramData data) {
        JsonHologramData hologramData = hologramDataToJson(data);
        JsonDisplayHologramData displayHologramData = displayHologramDataToJson(data);
        JsonItemHologramData itemHologramData = itemHologramDataToJson(data);

        return new JsonDataUnion(
                hologramData,
                displayHologramData,
                null,
                itemHologramData,
                null
        );
    }

    public static JsonDataUnion toUnion(BlockHologramData data) {
        JsonHologramData hologramData = hologramDataToJson(data);
        JsonDisplayHologramData displayHologramData = displayHologramDataToJson(data);
        JsonBlockHologramData blockHologramData = blockHologramDataToJson(data);

        return new JsonDataUnion(
                hologramData,
                displayHologramData,
                null,
                null,
                blockHologramData
        );
    }

    public static HologramData fromJson(JsonDataUnion data) {
        if (!data.hologram_data().world_name().equals(data.hologram_data().location().world())) {
            throw new IllegalArgumentException("World name in hologram data does not match location world");
        }

        Location loc = new Location(
                Bukkit.getWorld(data.hologram_data().world_name()),
                data.hologram_data().location().x(),
                data.hologram_data().location().y(),
                data.hologram_data().location().z(),
                data.hologram_data().location().yaw(),
                data.hologram_data().location().pitch()
        );
        Vector3f scale = new Vector3f(
                data.display_data().scale().x(),
                data.display_data().scale().y(),
                data.display_data().scale().z()
        );
        Vector3f translation = new Vector3f(
                data.display_data().translation().x(),
                data.display_data().translation().y(),
                data.display_data().translation().z()
        );

        Display.Brightness brightness = null;
        if (data.display_data().brightness() != null && data.display_data().brightness().sky_light() != null) {
            brightness = new Display.Brightness(
                    data.display_data().brightness().block_light(),
                    data.display_data().brightness().sky_light()
            );
        }

        HologramData hologramData = switch (data.hologram_data().type()) {
            case TEXT ->
                    new TextHologramData(data.hologram_data().name(), loc)
                            .setText(data.text_data().text()) // text data
                            .setBackground(data.text_data().background_color())
                            .setTextAlignment(data.text_data().text_alignment())
                            .setTextShadow(data.text_data().text_shadow())
                            .setSeeThrough(data.text_data().see_through())
                            .setTextUpdateInterval(data.text_data().text_update_interval())
                            .setTextOpacity(data.text_data().text_opacity() != null ? data.text_data().text_opacity().byteValue() : TextHologramData.DEFAULT_TEXT_OPACITY)
                            .setBillboard(data.display_data().billboard()) // display data
                            .setScale(scale)
                            .setTranslation(translation)
                            .setBrightness(brightness)
                            .setShadowRadius(data.display_data().shadow_radius())
                            .setShadowStrength(data.display_data().shadow_strength())
                            .setWorldName(data.hologram_data().world_name())// hologram data
                            .setVisibilityDistance(data.hologram_data().visibility_distance())
                            .setVisibility(data.hologram_data().visibility())
                            .setLinkedNpcName(data.hologram_data().linked_npc_name());

            case ITEM ->
                    new ItemHologramData(data.hologram_data().name(), loc)
                            .setItemStack(ItemStack.deserializeBytes(Base64.getDecoder().decode(data.item_data().item()))) // item data
                            .setBillboard(data.display_data().billboard()) // display data
                            .setScale(scale)
                            .setTranslation(translation)
                            .setBrightness(brightness)
                            .setShadowRadius(data.display_data().shadow_radius())
                            .setShadowStrength(data.display_data().shadow_strength())
                            .setGlowingColor(data.display_data().glowing_color() != null ? data.display_data().glowing_color() : DisplayHologramData.DEFAULT_GLOWING_COLOR)
                            .setWorldName(data.hologram_data().world_name())// hologram data
                            .setVisibilityDistance(data.hologram_data().visibility_distance())
                            .setVisibility(data.hologram_data().visibility())
                            .setLinkedNpcName(data.hologram_data().linked_npc_name());
            case BLOCK -> {
                BlockHologramData blockHologramData = new BlockHologramData(data.hologram_data().name(), loc);
                String blockMatOrData = data.block_data().block_material();
                try {
                    blockHologramData.setBlockData(Bukkit.createBlockData(blockMatOrData));
                } catch (Exception e) {
                    Material mat = Material.getMaterial(blockMatOrData.toUpperCase());
                    if (mat == null) {
                        mat = BlockHologramData.DEFAULT_BLOCK;
                    }
                    blockHologramData.setBlock(mat);
                }
                yield blockHologramData
                        .setBillboard(data.display_data().billboard()) // display data
                        .setScale(scale)
                        .setTranslation(translation)
                        .setBrightness(brightness)
                        .setShadowRadius(data.display_data().shadow_radius())
                        .setShadowStrength(data.display_data().shadow_strength())
                        .setGlowingColor(data.display_data().glowing_color() != null ? data.display_data().glowing_color() : DisplayHologramData.DEFAULT_GLOWING_COLOR)
                        .setWorldName(data.hologram_data().world_name())// hologram data
                        .setVisibilityDistance(data.hologram_data().visibility_distance())
                        .setVisibility(data.hologram_data().visibility())
                        .setLinkedNpcName(data.hologram_data().linked_npc_name());
            }
        };

        for (String traitName : data.hologram_data().traits()) {
            HologramTraitRegistry.TraitInfo traitInfo = FancyHolograms.get().getTraitRegistry().getTrait(traitName);
            if (traitInfo == null) {
                FancyHolograms.get().getFancyLogger().warn("Trait " + traitName + " is not registered");
                continue;
            }

            try {
                HologramTrait trait = traitInfo.clazz().getConstructor().newInstance();
                hologramData.getTraitTrait().addTrait(trait);
            } catch (Exception e) {
                FancyHolograms.get().getFancyLogger().error("Failed to instantiate trait " + traitName, ThrowableProperty.of(e));
            }
        }

        return hologramData;
    }
}
