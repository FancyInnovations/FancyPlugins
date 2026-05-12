package com.fancyinnovations.fancynpcs.tests.api;

import de.oliver.plugintests.annotations.FPTest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.lushplugins.chatcolorhandler.ChatColorHandler;
import org.lushplugins.chatcolorhandler.parsers.ParserTypes;

public class ChatColorHandlerTest {

    @FPTest(name = "Test Placeholders")
    public void testPlaceholders(Player player) {
        if (!isPlaceholderAPIEnabled()) {
            return;
        }

        String input = "Player name: %fn-test%";
        String got = ChatColorHandler.translate(input, player, ParserTypes.placeholder());
        String expected = "Player name: ";

        if (got.equalsIgnoreCase("Player name: %fn-test%")) {
            throw new AssertionError("Placeholder not replaced: " + got);
        }
    }

    private boolean isPlaceholderAPIEnabled() {
        Plugin placeholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        return placeholderAPI != null && placeholderAPI.isEnabled();
    }

}
