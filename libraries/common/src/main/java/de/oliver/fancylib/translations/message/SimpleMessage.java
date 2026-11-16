package de.oliver.fancylib.translations.message;

import de.oliver.fancylib.translations.TextConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class SimpleMessage extends Message {

    private String message;

    public SimpleMessage(TextConfig config, String message) {
        super(config);
        this.message = message;

        applyColorPlaceholders();
    }

    @Override
    public SimpleMessage replace(String placeholder, String replacement) {
        message = message
                .replace("{" + placeholder + "}", replacement)
                .replace("%" + placeholder + "%", replacement);

        return this;
    }

    @Override
    public Message replaceStripped(String placeholder, String replacement) {
        return replace(placeholder, MiniMessage.miniMessage().stripTags(replacement, tagResolverBuilder.build()));
    }

    @Override
    public SimpleMessage withPrefix() {
        if (message.isBlank()) {
            return this;
        }

        message = config.prefix() + message;
        return this;
    }

    @Override
    public SimpleMessage primary() {
        message = "<color:" + config.primaryColor() + ">" + message;
        return this;
    }

    @Override
    public SimpleMessage secondary() {
        message = "<color:" + config.secondaryColor() + ">" + message;
        return this;
    }

    @Override
    public SimpleMessage success() {
        message = "<color:" + config.successColor() + ">" + message;
        return this;
    }

    @Override
    public SimpleMessage warning() {
        message = "<color:" + config.warningColor() + ">" + message;
        return this;
    }

    @Override
    public SimpleMessage error() {
        message = "<color:" + config.errorColor() + ">" + message;
        return this;
    }

    @Override
    public SimpleMessage applyCustomPlaceholders() {
        // TODO: add ChatColorHandler support

        return this;
    }

    @Override
    public Component buildComponent() {
        return MiniMessage.miniMessage().deserialize(message, tagResolverBuilder.build());
    }

    @Override
    public Message copy() {
        return new SimpleMessage(config, message);
    }

    public String build() {
        return message;
    }

    public void actionbar(Player receiver) {
        receiver.sendActionBar(buildComponent());
    }

    public String getMessage() {
        return message;
    }
}
