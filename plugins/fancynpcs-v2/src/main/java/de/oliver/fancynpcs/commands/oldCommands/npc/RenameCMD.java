package de.oliver.fancynpcs.commands.oldCommands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.regex.Pattern;

public enum RenameCMD {
    INSTANCE; // SINGLETON

    private static final Pattern NPC_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9/_-]*$");
    private static final UUID EMPTY_UUID = new UUID(0, 0);
    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc rename <npc> <name>")
    @Permission("fancynpcs.command.npc.rename")
    public void onRename(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull String name
    ) {
        if (!NPC_NAME_PATTERN.matcher(name).find()) {
            translator.translate("npc_create_failure_invalid_name").withPrefix().replaceStripped("name", name).send(sender);
            return;
        }

        final UUID creator = (sender instanceof Player player) ? player.getUniqueId() : EMPTY_UUID;
        final boolean alreadyExists = FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled()
                ? FancyNpcs.getInstance().getNpcManager().getNpc(name, creator) != null
                : FancyNpcs.getInstance().getNpcManager().getNpc(name) != null;
        if (alreadyExists) {
            translator.translate("npc_create_failure_already_exists").withPrefix().replace("npc", name).send(sender);
            return;
        }

        final String oldName = npc.getData().getName();
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.NAME, name, sender).callEvent()) {
            FancyNpcs.getInstance().getNpcManager().renameNpc(npc, name);
            translator.translate("npc_rename_success").withPrefix().replace("npc", oldName).replace("new_npc", name).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
        }
    }
}
