package de.oliver.fancynpcs.commands.lampCommands.types;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.data.property.NpcVisibility;
import de.oliver.fancynpcs.commands.lampCommands.npc.ListCMD;
import de.oliver.fancynpcs.commands.lampCommands.npc.NearbyCMD;
import de.oliver.fancynpcs.utils.GlowingColor;
import org.bukkit.entity.EntityType;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.BukkitExceptionHandler;
import revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import revxrsal.commands.exception.EnumNotFoundException;
import revxrsal.commands.exception.NoPermissionException;

public final class FancyNpcsExceptionHandler extends BukkitExceptionHandler {

    private final FancyNpcs plugin;

    public FancyNpcsExceptionHandler(FancyNpcs plugin) {
        this.plugin = plugin;
    }

    @HandleException
    public void onFancyNpcsParameter(FancyNpcsParameterException exception, BukkitCommandActor actor) {
        plugin.getTranslator().translate(exception.translationKey())
                .withPrefix()
                .replaceStripped("input", exception.input())
                .send(actor.sender());
    }

    @Override
    public void onEnumNotFound(EnumNotFoundException exception, BukkitCommandActor actor) {
        String key = "command_invalid_enum_generic";
        if (exception.enumType() == ListCMD.SortType.class) {
            key = "command_invalid_list_sort_type";
        } else if (exception.enumType() == NearbyCMD.SortType.class) {
            key = "command_invalid_nearby_sort_type";
        } else if (exception.enumType() == EntityType.class) {
            key = "command_invalid_entity_type";
        } else if (exception.enumType() == GlowingColor.class) {
            key = "command_invalid_glowing_color";
        } else if (exception.enumType() == NpcVisibility.class) {
            key = "command_invalid_npc_visibility";
        }

        plugin.getTranslator().translate(key)
                .withPrefix()
                .replaceStripped("input", exception.input())
                .replace("enum", exception.enumType().getSimpleName().toLowerCase())
                .send(actor.sender());
    }

    @Override
    public void onNoPermission(NoPermissionException exception, BukkitCommandActor actor) {
        plugin.getTranslator().translate("command_missing_permissions").withPrefix().send(actor.sender());
    }

    @Override
    public void onSenderNotPlayer(SenderNotPlayerException exception, BukkitCommandActor actor) {
        plugin.getTranslator().translate("command_player_only").withPrefix().send(actor.sender());
    }
}
