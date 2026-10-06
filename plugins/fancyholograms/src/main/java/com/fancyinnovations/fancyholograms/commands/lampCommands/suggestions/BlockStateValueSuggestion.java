package com.fancyinnovations.fancyholograms.commands.lampCommands.suggestions;

import com.fancyinnovations.fancyholograms.api.data.BlockHologramData;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import org.bukkit.Instrument;
import org.bukkit.block.data.*;
import org.bukkit.block.data.type.*;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class BlockStateValueSuggestion implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull Collection<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        Hologram hologram = context.getResolvedArgumentOrNull(Hologram.class);
        if (hologram == null || !(hologram.getData() instanceof BlockHologramData blockHoloData)) {
            return List.of();
        }

        String property = context.getResolvedArgumentOrNull("property");
        if (property == null) {
            return List.of();
        }

        BlockData blockData = blockHoloData.getBlockData();
        if (blockData == null) {
            return List.of();
        }

        Map<String, String> properties = blockHoloData.getBlockStateProperties();
        String currentValue = properties.get(property.toLowerCase());
        if (currentValue != null && (currentValue.equalsIgnoreCase("true") || currentValue.equalsIgnoreCase("false"))) {
            return List.of("true", "false");
        }

        if (blockData instanceof Directional directional && property.equalsIgnoreCase("facing")) {
            return directional.getFaces().stream().map(f -> f.name().toLowerCase()).toList();
        }
        if (blockData instanceof Orientable orientable && property.equalsIgnoreCase("axis")) {
            return orientable.getAxes().stream().map(a -> a.name().toLowerCase()).toList();
        }
        if (blockData instanceof Rotatable && property.equalsIgnoreCase("rotation")) {
            return IntStream.rangeClosed(0, 15).mapToObj(Integer::toString).toList();
        }
        if (blockData instanceof StructureBlock && property.equalsIgnoreCase("mode")) {
            return Arrays.stream(StructureBlock.Mode.values()).map(m -> m.name().toLowerCase()).toList();
        }
        if (blockData instanceof Stairs && property.equalsIgnoreCase("shape")) {
            return Arrays.stream(Stairs.Shape.values()).map(s -> s.name().toLowerCase()).toList();
        }
        if (blockData instanceof Slab && property.equalsIgnoreCase("type")) {
            return Arrays.stream(Slab.Type.values()).map(t -> t.name().toLowerCase()).toList();
        }
        if (blockData instanceof Chest && property.equalsIgnoreCase("type")) {
            return Arrays.stream(Chest.Type.values()).map(t -> t.name().toLowerCase()).toList();
        }
        if (blockData instanceof Door && property.equalsIgnoreCase("hinge")) {
            return Arrays.stream(Door.Hinge.values()).map(h -> h.name().toLowerCase()).toList();
        }
        if (blockData instanceof Bisected && property.equalsIgnoreCase("half")) {
            return Arrays.stream(Bisected.Half.values()).map(h -> h.name().toLowerCase()).toList();
        }
        if (blockData instanceof Bed && property.equalsIgnoreCase("part")) {
            return Arrays.stream(Bed.Part.values()).map(p -> p.name().toLowerCase()).toList();
        }
        if (blockData instanceof Ageable ageable && property.equalsIgnoreCase("age")) {
            return IntStream.rangeClosed(0, ageable.getMaximumAge()).mapToObj(Integer::toString).toList();
        }
        if (blockData instanceof Levelled levelled && property.equalsIgnoreCase("level")) {
            return IntStream.rangeClosed(0, levelled.getMaximumLevel()).mapToObj(Integer::toString).toList();
        }
        if (blockData instanceof AnaloguePowerable ap && property.equalsIgnoreCase("power")) {
            return IntStream.rangeClosed(0, ap.getMaximumPower()).mapToObj(Integer::toString).toList();
        }
        if (blockData instanceof FaceAttachable && (property.equalsIgnoreCase("face") || property.equalsIgnoreCase("attachment"))) {
            return Arrays.stream(FaceAttachable.AttachedFace.values()).map(f -> f.name().toLowerCase()).toList();
        }
        if (blockData instanceof Bell && property.equalsIgnoreCase("attachment")) {
            return Arrays.stream(Bell.Attachment.values()).map(a -> a.name().toLowerCase()).toList();
        }
        if (blockData instanceof RedstoneWire rw) {
            if (property.equalsIgnoreCase("north") || property.equalsIgnoreCase("south") || property.equalsIgnoreCase("east") || property.equalsIgnoreCase("west")) {
                return Arrays.stream(RedstoneWire.Connection.values()).map(c -> c.name().toLowerCase()).toList();
            }
            if (property.equalsIgnoreCase("power")) {
                return IntStream.rangeClosed(0, rw.getMaximumPower()).mapToObj(Integer::toString).toList();
            }
        }
        if (blockData instanceof Snow snow && property.equalsIgnoreCase("layers")) {
            return IntStream.rangeClosed(1, snow.getMaximumLayers()).mapToObj(Integer::toString).toList();
        }
        if (blockData instanceof Cake cake && property.equalsIgnoreCase("bites")) {
            return IntStream.rangeClosed(0, cake.getMaximumBites()).mapToObj(Integer::toString).toList();
        }
        if (blockData instanceof TechnicalPiston && property.equalsIgnoreCase("type")) {
            return Arrays.stream(TechnicalPiston.Type.values()).map(t -> t.name().toLowerCase()).toList();
        }
        if (blockData instanceof Bamboo bamboo) {
            if (property.equalsIgnoreCase("leaves")) {
                return Arrays.stream(Bamboo.Leaves.values()).map(l -> l.name().toLowerCase()).toList();
            }
            if (property.equalsIgnoreCase("age")) {
                return IntStream.rangeClosed(0, bamboo.getMaximumAge()).mapToObj(Integer::toString).toList();
            }
            if (property.equalsIgnoreCase("stage")) {
                return List.of("0", "1");
            }
        }
        if (blockData instanceof NoteBlock && property.equalsIgnoreCase("instrument")) {
            return Arrays.stream(Instrument.values()).map(i -> i.name().toLowerCase()).toList();
        }
        if (blockData instanceof NoteBlock && property.equalsIgnoreCase("note")) {
            return IntStream.rangeClosed(0, 24).mapToObj(Integer::toString).toList();
        }
        if (blockData instanceof Rail && property.equalsIgnoreCase("shape")) {
            return Arrays.stream(Rail.Shape.values()).map(s -> s.name().toLowerCase()).toList();
        }

        return List.of();
    }
}
