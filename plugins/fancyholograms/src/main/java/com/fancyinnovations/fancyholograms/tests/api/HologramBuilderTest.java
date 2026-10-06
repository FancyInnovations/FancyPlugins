package com.fancyinnovations.fancyholograms.tests.api;

import com.fancyinnovations.fancyholograms.api.HologramRegistry;
import com.fancyinnovations.fancyholograms.api.data.BlockHologramData;
import com.fancyinnovations.fancyholograms.api.data.ItemHologramData;
import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.data.builder.BlockHologramBuilder;
import com.fancyinnovations.fancyholograms.api.data.builder.ItemHologramBuilder;
import com.fancyinnovations.fancyholograms.api.data.builder.TextHologramBuilder;
import com.fancyinnovations.fancyholograms.api.data.property.Visibility;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import com.fancyinnovations.fancyholograms.trait.builtin.DebugTrait;
import de.oliver.plugintests.annotations.FPTest;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

import static de.oliver.plugintests.Expectable.expect;

public class HologramBuilderTest {

    @FPTest(name = "Test basic text hologram builder")
    public void testTextHologramBuilder(Player player) {
        Hologram hologram = TextHologramBuilder.create("Test", player.getLocation())
                .build();

        expect(hologram).toBeDefined();
        expect(hologram.getData()).toBeDefined();
        expect(hologram.getData().getName()).toBe("Test");
        expect(hologram.getData().getLocation()).toEqual(player.getLocation());
        expect(hologram.getData().getType()).toEqual(HologramType.TEXT);

        if (!(hologram.getData() instanceof TextHologramData data)) {
            throw new AssertionError("Hologram is not a text hologram");
        }
        expect(data.getTextOpacity()).toEqual(TextHologramData.DEFAULT_TEXT_OPACITY);
    }

    @FPTest(name = "Test text hologram builder with one line")
    public void testTextHologramBuilderWithOneLine(Player player) {
        Hologram hologram = TextHologramBuilder.create("Test", player.getLocation())
                .text("Custom line")
                .build();

        if (!(hologram.getData() instanceof TextHologramData data)) {
            throw new AssertionError("Hologram is not a text hologram");
        }

        expect(data.getText().size()).toEqual(1);
        expect(data.getText().getFirst()).toEqual("Custom line");
    }

    @FPTest(name = "Test text hologram builder with multiple line")
    public void testTextHologramBuilderWithMultipleLines(Player player) {
        Hologram hologram = TextHologramBuilder.create("Test", player.getLocation())
                .text("Custom line", "Another line", "Yet another line")
                .build();

        if (!(hologram.getData() instanceof TextHologramData data)) {
            throw new AssertionError("Hologram is not a text hologram");
        }

        expect(data.getText().size()).toEqual(3);
        expect(data.getText().getFirst()).toEqual("Custom line");
        expect(data.getText().get(1)).toEqual("Another line");
        expect(data.getText().getLast()).toEqual("Yet another line");
    }

    @FPTest(name = "Test text hologram builder with multiple line 2")
    public void testTextHologramBuilderWithMultipleLines2(Player player) {
        Hologram hologram = TextHologramBuilder.create("Test", player.getLocation())
                .text(List.of("Custom line", "Another line", "Yet another line"))
                .build();

        if (!(hologram.getData() instanceof TextHologramData data)) {
            throw new AssertionError("Hologram is not a text hologram");
        }

        expect(data.getText().size()).toEqual(3);
        expect(data.getText().getFirst()).toEqual("Custom line");
        expect(data.getText().get(1)).toEqual("Another line");
        expect(data.getText().getLast()).toEqual("Yet another line");
    }

    @FPTest(name = "Test text hologram builder with properties")
    public void testTextHologramBuilderWithProperties(Player player) {
        Hologram hologram = TextHologramBuilder.create("Test", player.getLocation())
                .text("Custom line")
                .background(Color.BLACK)
                .textAlignment(TextDisplay.TextAlignment.LEFT)
                .textShadow(true)
                .seeThrough(true)
                .updateTextInterval(420)
                .textOpacity(128)
                .visibilityDistance(42)
                .visibility(Visibility.ALL)
                .persistent(false)
                .linkedNpcName("TestNPC")
                .trait(DebugTrait.class)
                .billboard(Display.Billboard.FIXED)
                .scale(3, 5, 6)
                .translation(1, 2, 3)
                .brightness(7, 3)
                .shadowRadius(0.5f)
                .shadowStrength(0.7f)
                .interpolationDuration(100)
                .build();

        if (!(hologram.getData() instanceof TextHologramData data)) {
            throw new AssertionError("Hologram is not a text hologram");
        }

        expect(data.getText().size()).toEqual(1);
        expect(data.getText().getFirst()).toEqual("Custom line");
        expect(data.getBackground()).toEqual(Color.BLACK);
        expect(data.getTextAlignment()).toEqual(TextDisplay.TextAlignment.LEFT);
        expect(data.hasTextShadow()).toBe(true);
        expect(data.isSeeThrough()).toBe(true);
        expect(data.getTextUpdateInterval()).toEqual(420);
        expect(data.getTextOpacity()).toEqual((byte) 128);
        expect(data.getVisibilityDistance()).toEqual(42);
        expect(data.getVisibility()).toEqual(Visibility.ALL);
        expect(data.isPersistent()).toBe(false);
        expect(data.getLinkedNpcName()).toEqual("TestNPC");
        expect(data.getTraitTrait().isTraitAttached(DebugTrait.class)).toBe(true);
        expect(data.getBillboard()).toEqual(Display.Billboard.FIXED);
        expect(data.getScale()).toEqual(new Vector3f(3, 5, 6));
        expect(data.getTranslation()).toEqual(new Vector3f(1, 2, 3));
        expect(data.getBrightness()).toEqual(new Display.Brightness(7, 3));
        expect(data.getShadowRadius()).toEqual(0.5f);
        expect(data.getShadowStrength()).toEqual(0.7f);
        expect(data.getInterpolationDuration()).toEqual(100);
    }

    @FPTest(name = "Test text hologram builder with text opacity")
    public void testTextHologramBuilderWithTextOpacity(Player player) {
        Hologram hologram = TextHologramBuilder.create("Test", player.getLocation())
                .textOpacity(200)
                .build();

        if (!(hologram.getData() instanceof TextHologramData data)) {
            throw new AssertionError("Hologram is not a text hologram");
        }

        expect(data.getTextOpacity()).toEqual((byte) 200);

        TextHologramData copy = data.copy("TestCopy");
        expect(copy.getTextOpacity()).toEqual((byte) 200);
    }

    @FPTest(name = "Test item hologram builder")
    public void testItemHologramBuilder(Player player) {
        ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);

        Hologram hologram = ItemHologramBuilder.create("Test", player.getLocation())
                .item(item)
                .build();

        expect(hologram).toBeDefined();
        expect(hologram.getData()).toBeDefined();
        expect(hologram.getData().getName()).toBe("Test");
        expect(hologram.getData().getLocation()).toEqual(player.getLocation());
        expect(hologram.getData().getType()).toEqual(HologramType.ITEM);

        if (!(hologram.getData() instanceof ItemHologramData data)) {
            throw new AssertionError("Hologram is not an item hologram");
        }

        expect(data.getItemStack()).toEqual(item);
    }

    @FPTest(name = "Test block hologram builder")
    public void testBlockHologramBuilder(Player player) {
        Hologram hologram = BlockHologramBuilder.create("Test", player.getLocation())
                .block(Material.DIRT)
                .build();

        expect(hologram).toBeDefined();
        expect(hologram.getData()).toBeDefined();
        expect(hologram.getData().getName()).toBe("Test");
        expect(hologram.getData().getLocation()).toEqual(player.getLocation());
        expect(hologram.getData().getType()).toEqual(HologramType.BLOCK);

        if (!(hologram.getData() instanceof BlockHologramData data)) {
            throw new AssertionError("Hologram is not a block hologram");
        }

        expect(data.getBlock()).toEqual(Material.DIRT);
    }

    @FPTest(name = "Test block hologram builder with block state property")
    public void testBlockHologramBuilderWithBlockStateProperty(Player player) {
        Hologram hologram = BlockHologramBuilder.create("Test", player.getLocation())
                .block(Material.STRUCTURE_BLOCK, "mode", "save")
                .build();

        if (!(hologram.getData() instanceof BlockHologramData data)) {
            throw new AssertionError("Hologram is not a block hologram");
        }

        expect(data.getBlock()).toEqual(Material.STRUCTURE_BLOCK);
        expect(data.getBlockStateProperties().get("mode")).toEqual("save");
    }

    @FPTest(name = "Test block hologram builder with block state fluent method")
    public void testBlockHologramBuilderWithBlockStateFluent(Player player) {
        Hologram hologram = BlockHologramBuilder.create("Test", player.getLocation())
                .block(Material.CHEST)
                .blockState("facing", "south")
                .build();

        if (!(hologram.getData() instanceof BlockHologramData data)) {
            throw new AssertionError("Hologram is not a block hologram");
        }

        expect(data.getBlock()).toEqual(Material.CHEST);
        expect(data.getBlockStateProperties().get("facing")).toEqual("south");

        BlockHologramData copy = data.copy("TestCopy");
        expect(copy.getBlock()).toEqual(Material.CHEST);
        expect(copy.getBlockStateProperties().get("facing")).toEqual("south");

        // Mutating copy should not mutate original
        copy.setBlockStateProperty("facing", "north");
        expect(copy.getBlockStateProperties().get("facing")).toEqual("north");
        expect(data.getBlockStateProperties().get("facing")).toEqual("south");
    }

    @FPTest(name = "Test hologram builder registering")
    public void testHologramBuilderRegistering(Player player) {
        String hologramName = UUID.randomUUID().toString();

        Hologram hologram = TextHologramBuilder.create(hologramName, player.getLocation())
                .text("Custom line")
                .buildAndRegister();

        expect(hologram).toBeDefined();
        expect(hologram.getData()).toBeDefined();
        expect(hologram.getData().getName()).toBe(hologramName);
        expect(hologram.getData().getLocation()).toEqual(player.getLocation());
        expect(hologram.getData().getType()).toEqual(HologramType.TEXT);

        HologramRegistry registry = FancyHologramsPlugin.get().getRegistry();
        expect(registry.contains(hologramName)).toBe(true);

        registry.unregister(hologram);
    }

}
