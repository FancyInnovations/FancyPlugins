package de.oliver.fancyvisuals.chat;

import de.oliver.fancyvisuals.FancyVisuals;
import de.oliver.fancyvisuals.utils.TextRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListeners implements Listener {

    private static final String MESSAGE_TOKEN = "{message}";

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        if (!FancyVisuals.get().getFancyVisualsConfig().isChatEnabled()) {
            return;
        }

        Player player = event.getPlayer();
        ChatFormat format = FancyVisuals.get().getChatFormatRepository().getFormatForPlayer(player);
        String rawFormat = format.format();
        Component base = TextRenderer.render(rawFormat, player);
        boolean hasMessageToken = rawFormat.contains(MESSAGE_TOKEN);

        event.renderer((source, sourceDisplayName, message, viewer) -> {
            if (hasMessageToken) {
                return base.replaceText(TextReplacementConfig.builder()
                        .matchLiteral(MESSAGE_TOKEN)
                        .replacement(message)
                        .build());
            }

            return base.append(Component.space()).append(message);
        });
    }
}
