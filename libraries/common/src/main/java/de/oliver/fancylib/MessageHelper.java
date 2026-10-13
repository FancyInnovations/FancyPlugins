package de.oliver.fancylib;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class MessageHelper {
    private static String primaryColor = "#59bdff";
    private static String secondaryColor = "#6696e3";
    private static String successColor = "#81e366";
    private static String warningColor = "#e3ca66";
    private static String errorColor = "#e36666";

    @Deprecated
    public static @NotNull String transform(@NotNull String message) {
        message = message.replace("<primaryColor>", "<color:" + primaryColor + ">")
                .replace("<secondaryColor>", "<color:" + secondaryColor + ">")
                .replace("<successColor>", "<color:" + successColor + ">")
                .replace("<warningColor>", "<color:" + warningColor + ">")
                .replace("<errorColor>", "<color:" + errorColor + ">");

        return message;
    }

    @Deprecated
    public static @NotNull Component send(@Nullable CommandSender receiver, @NotNull String message) {
        Component msg = MiniMessage.miniMessage().deserialize(transform(message));
        if (receiver != null) {
            receiver.sendMessage(msg);
        }
        return msg;
    }

    @Deprecated
    public static @NotNull Component info(@Nullable CommandSender receiver, @NotNull String message) {
        Component msg = MiniMessage.miniMessage().deserialize(transform("<primaryColor>" + message));
        if (receiver != null) {
            receiver.sendMessage(msg);
        }
        return msg;
    }

    @Deprecated
    public static @NotNull Component success(@Nullable CommandSender receiver, @NotNull String message) {
        Component msg = MiniMessage.miniMessage().deserialize(transform("<successColor>" + message));
        if (receiver != null) {
            receiver.sendMessage(msg);
        }
        return msg;
    }

    @Deprecated
    public static @NotNull Component warning(@Nullable CommandSender receiver, @NotNull String message) {
        Component msg = MiniMessage.miniMessage().deserialize(transform("<warningColor>" + message));
        if (receiver != null) {
            receiver.sendMessage(msg);
        }
        return msg;
    }

    @Deprecated
    public static @NotNull Component error(@Nullable CommandSender receiver, @NotNull String message) {
        Component msg = MiniMessage.miniMessage().deserialize(transform("<errorColor>" + message));
        if (receiver != null) {
            receiver.sendMessage(msg);
        }
        return msg;
    }

    @Deprecated
    public static String getPrimaryColor() {
        return primaryColor;
    }

    @Deprecated
    public static void setPrimaryColor(String primaryColor) {
        MessageHelper.primaryColor = primaryColor;
    }

    @Deprecated
    public static String getSecondaryColor() {
        return secondaryColor;
    }

    @Deprecated
    public static void setSecondaryColor(String secondaryColor) {
        MessageHelper.secondaryColor = secondaryColor;
    }

    @Deprecated
    public static String getSuccessColor() {
        return successColor;
    }

    @Deprecated
    public static void setSuccessColor(String successColor) {
        MessageHelper.successColor = successColor;
    }

    @Deprecated
    public static String getWarningColor() {
        return warningColor;
    }

    @Deprecated
    public static void setWarningColor(String warningColor) {
        MessageHelper.warningColor = warningColor;
    }

    @Deprecated
    public static String getErrorColor() {
        return errorColor;
    }

    @Deprecated
    public static void setErrorColor(String errorColor) {
        MessageHelper.errorColor = errorColor;
    }

    @Deprecated
    public static Component removeDecoration(Component component, TextDecoration decoration) {
        return component.decoration(decoration, TextDecoration.State.FALSE);
    }
}
