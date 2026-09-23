package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.api.skins.SkinData;
import de.oliver.fancynpcs.api.skins.SkinLoadException;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import de.oliver.fancynpcs.commands.lampCommands.suggestions.SkinSuggestion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Flag;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class SkinCMD extends FancyContext {
    public static final SkinCMD INSTANCE = new SkinCMD();

    private SkinCMD() {
    }


    /* PARSERS AND SUGGESTIONS */

    @Command("npc skin <npc> <skin>")
    @CommandPermission("fancynpcs.command.npc.skin")
    public void onSkin(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final @NotNull @SuggestWith(SkinSuggestion.class) String skin,
            final @Optional @Flag("slim") boolean slim
    ) {
        final CommandSender sender = actor.sender();
        if (npc.getData().getType() != EntityType.PLAYER) {
            translator.translate("command_unsupported_npc_type").withPrefix().send(sender);
            return;
        }

        final boolean isMirror = skin.equalsIgnoreCase("@mirror");
        final boolean isNone = skin.equalsIgnoreCase("@none");
        if (isMirror) {
            if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.MIRROR_SKIN, true, sender).callEvent()) {
                npc.getData().setMirrorSkin(true);
                npc.removeForAll();
                npc.create();
                npc.spawnForAll();
                translator.translate("npc_skin_set_mirror").replace("npc", npc.getData().getName()).withPrefix().send(sender);
            } else {
                translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
            }
        } else if (isNone) {
            if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, false, sender).callEvent() && new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, null, sender).callEvent()) {
                npc.getData().setMirrorSkin(false);
                npc.getData().setSkinData(null);
                npc.removeForAll();
                npc.create();
                npc.spawnForAll();
                translator.translate("npc_skin_set_none").withPrefix().replace("npc", npc.getData().getName()).send(sender);
            } else {
                translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
            }
        } else try {
            SkinData.SkinVariant variant = slim ? SkinData.SkinVariant.SLIM : SkinData.SkinVariant.AUTO;
            SkinData skinData = plugin.getSkinManagerImpl().getByIdentifier(skin, variant);
            skinData.setIdentifier(skin);

            if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, false, sender).callEvent() && new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, skinData, sender).callEvent()) {
                translator.translate("npc_skin_set")
                        .withPrefix()
                        .replace("npc", npc.getData().getName())
                        .replace("name", skinData.getIdentifier())
                        .send(sender);
                if (!skinData.hasTexture()) {
                    translator.translate("npc_skin_set_later").withPrefix().replace("npc", npc.getData().getName()).send(sender);
                }
                npc.getData().setMirrorSkin(false);
                npc.getData().setSkinData(skinData);
                npc.removeForAll();
                npc.create();
                npc.spawnForAll();

            } else {
                translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
            }
        } catch (final SkinLoadException e) {
            switch (e.getReason()) {
                case INVALID_URL ->
                        translator.translate("npc_skin_failure_invalid_url").withPrefix().replace("npc", npc.getData().getName()).send(sender);
                case INVALID_FILE ->
                        translator.translate("npc_skin_failure_invalid_file").withPrefix().replace("npc", npc.getData().getName()).send(sender);
                case INVALID_USERNAME ->
                        translator.translate("npc_skin_failure_invalid_username").withPrefix().replace("npc", npc.getData().getName()).send(sender);
                case INVALID_PLACEHOLDER ->
                        translator.translate("npc_skin_failure_invalid_placeholder").withPrefix().replace("npc", npc.getData().getName()).send(sender);
            }
        }
    }

}
