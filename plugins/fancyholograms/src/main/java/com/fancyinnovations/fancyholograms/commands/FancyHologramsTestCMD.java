package com.fancyinnovations.fancyholograms.commands;

import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import com.fancyinnovations.fancyholograms.tests.FHTests;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class FancyHologramsTestCMD extends Command {

    @NotNull
    private final FancyHologramsPlugin plugin;

    public FancyHologramsTestCMD(@NotNull final FancyHologramsPlugin plugin) {
        super("FancyHologramsTest");
        setPermission("fancyholograms.admin");
        this.plugin = plugin;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        Player p = (Player) commandSender;

        FHTests tests = new FHTests();
        if (tests.runAllTests(p)) {
            MessageHelper.success(p, "All tests have been successfully run!");
        } else {
            MessageHelper.error(p, "There was an issue running the tests!");
        }

        return true;
    }

    //    @Override
//    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
//        if (args.length == 1) {
//            return Arrays.asList("spawn100", "test1");
//        }
//
//        return Collections.emptyList();
//    }

//    @Override
//    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
//        if (!testPermission(sender)) {
//            return false;
//        }
//
//        if (!(sender instanceof Player p)) {
//            MessageHelper.error(sender, "Only players can use this command!");
//            return false;
//        }
//
//        if (args.length == 1 && args[0].equalsIgnoreCase("spawn100")) {
//            for (int i = 0; i < 10; i++) {
//                for (int j = 0; j < 10; j++) {
//                    int n = (i * 10 + j) + 1;
//                    TextHologramData textData = new TextHologramData("holo-" + n, p.getLocation().clone().add(5 * i + 1, 0, 5 * j + 1));
//                    textData.setText(Arrays.asList(
//                            "<rainbow><b>This is a test hologram! (#" + n + ")</b></rainbow>",
//                            "<red>Lorem ipsum dolor sit amet, consec tetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris.",
//                            "<green>Lorem ipsum dolor sit amet, consec tetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris.",
//                            "<yellow>Lorem ipsum dolor sit amet, consec tetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris.",
//                            "<gradient:red:green>Lorem ipsum dolor sit amet, consec tetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris.",
//                            "<gradient:green:yellow>Lorem ipsum dolor sit amet, consec tetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris."
//                    ));
//                    textData.setTextUpdateInterval(100)
//                        .setScale(new Vector3f(.5f, .5f, .5f))
//                        .setVisibilityDistance(100);
//
//                    Hologram hologram = this.plugin.getHologramFactory().apply(textData);
//                    hologram.spawnTo(p);
//                }
//            }
//
//            return true;
//        } else if (args.length == 1 && args[0].equalsIgnoreCase("test1")) {
//            TextHologramData textData = new TextHologramData("holo-test1", p.getLocation());
//            textData.setText(Arrays.asList(
//                    "<rainbow><b>This is a test hologram!</b></rainbow>",
//                    "<red>Lorem ipsum dolor sit amet, consec tetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris.",
//                    "<green>Lorem ipsum dolor sit amet, consec tetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris.",
//                    "<yellow>Lorem ipsum dolor sit amet, consec tetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris.",
//                    "<gradient:red:green>Lorem ipsum dolor sit amet, consec tetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris.",
//                    "<gradient:green:yellow>Lorem ipsum dolor sit amet, consec tetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris."
//            ))
//                .setTextUpdateInterval(100)
//                .setTextAlignment(TextDisplay.TextAlignment.CENTER)
//                .setBackground(Color.fromARGB(15, 78, 237, 176))
//                .setTextShadow(true)
//                .setScale(new Vector3f(2, 2, 2))
//                .setBillboard(Display.Billboard.CENTER)
//                .setBrightness(new Display.Brightness(15, 15))
//                .setShadowRadius(3)
//                .setShadowStrength(3)
//                .setVisibilityDistance(100);
//
//            Hologram hologram = this.plugin.getHologramFactory().apply(textData);
//            hologram.spawnTo(p);
//        }
//
//        return false;
//    }
}
