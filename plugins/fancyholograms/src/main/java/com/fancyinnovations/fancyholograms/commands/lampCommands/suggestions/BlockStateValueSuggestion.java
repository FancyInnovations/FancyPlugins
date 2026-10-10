package com.fancyinnovations.fancyholograms.commands.lampCommands.suggestions;

import com.fancyinnovations.fancyholograms.api.data.BlockHologramData;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import org.bukkit.Axis;
import org.bukkit.Instrument;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.*;
import org.bukkit.block.data.type.*;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.*;
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

        String p = property.toLowerCase();
        return switch (blockData) {
            case Directional directional when p.equals("facing") -> names(directional.getFaces().toArray(BlockFace[]::new));
            case Orientable orientable when p.equals("axis") -> names(orientable.getAxes().toArray(Axis[]::new));
            case Rotatable _ when p.equals("rotation") -> range(0, 15);
            case StructureBlock _ when p.equals("mode") -> names(StructureBlock.Mode.values());
            case Stairs _ when p.equals("shape") -> names(Stairs.Shape.values());
            case Slab _ when p.equals("type") -> names(Slab.Type.values());
            case Chest _ when p.equals("type") -> names(Chest.Type.values());
            case Door _ when p.equals("hinge") -> names(Door.Hinge.values());
            case Bisected _ when p.equals("half") -> names(Bisected.Half.values());
            case Bed _ when p.equals("part") -> names(Bed.Part.values());
            case Bell _ when p.equals("attachment") -> names(Bell.Attachment.values());
            case Rail _ when p.equals("shape") -> names(Rail.Shape.values());
            case TechnicalPiston _ when p.equals("type") -> names(TechnicalPiston.Type.values());
            case FaceAttachable _ when p.equals("face") || p.equals("attachment") -> names(FaceAttachable.AttachedFace.values());
            case Ageable ageable when p.equals("age") -> range(0, ageable.getMaximumAge());
            case Levelled levelled when p.equals("level") -> range(0, levelled.getMaximumLevel());
            case AnaloguePowerable analoguePowerable when p.equals("power") -> range(0, analoguePowerable.getMaximumPower());
            case Snow snow when p.equals("layers") -> range(1, snow.getMaximumLayers());
            case Cake cake when p.equals("bites") -> range(0, cake.getMaximumBites());
            case RedstoneWire _ when Set.of("north", "south", "east", "west").contains(p) -> names(RedstoneWire.Connection.values());
            case Bamboo _ when p.equals("leaves") -> names(Bamboo.Leaves.values());
            case Bamboo _ when p.equals("stage") -> List.of("0", "1");
            case NoteBlock _ when p.equals("instrument") -> names(Instrument.values());
            case NoteBlock _ when p.equals("note") -> range(0, 24);
            default -> List.of();
        };
    }

    private List<String> names(Enum<?>[] values) {
        return Arrays.stream(values).map(anEnum -> anEnum.name().toLowerCase()).toList();
    }

    private List<String> range(int from, int to) {
        return IntStream.rangeClosed(from, to).mapToObj(Integer::toString).toList();
    }
}
