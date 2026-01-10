package com.fancyinnovations.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import com.fancyinnovations.fancynpcs.FancyNpcs;
import com.fancyinnovations.fancynpcs.api.Npc;
import com.fancyinnovations.fancynpcs.api.events.NpcModifyEvent;
import com.fancyinnovations.fancynpcs.api.skins.SkinData;
import com.fancyinnovations.fancynpcs.api.skins.SkinLoadException;
import com.fancyinnovations.fancynpcs.skins.SkinUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Flag;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum SkinCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    /* PARSERS AND SUGGESTIONS */

    @Command("npc skin <npc> <skin>")
    @Permission("fancynpcs.command.npc.skin")
    public void onSkin(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull @Argument(suggestions = "SkinCMD/skin") String skin,
            final @Flag("slim") boolean slim
    ) {
        if (npc.getData().getType() != EntityType.PLAYER) {
            translator.translate("command_unsupported_npc_type").send(sender);
            return;
        }

        final boolean isMirror = skin.equalsIgnoreCase("@mirror");
        final boolean isNone = skin.equalsIgnoreCase("@none");
        final boolean isTexturePack = skin.toLowerCase().startsWith("@texturepack:");

        if (isTexturePack) {
            // Texture pack skin: @texturepack:namespace:path/to/skin
            // Resolves to assets/<namespace>/textures/<path/to/skin>.png in resource pack
            String textureAsset = skin.substring("@texturepack:".length());
            if (textureAsset.isEmpty() || !textureAsset.contains(":")) {
                translator.translate("npc_skin_failure_invalid_texturepack")
                        .replace("npc", npc.getData().getName())
                        .send(sender);
                return;
            }

            SkinData.SkinVariant variant = slim ? SkinData.SkinVariant.SLIM : SkinData.SkinVariant.AUTO;
            SkinData skinData = SkinData.texturePackSkin(textureAsset, variant);

            if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, false, sender).callEvent()
                    && new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, skinData, sender).callEvent()) {
                npc.getData().setMirrorSkin(false);
                npc.getData().setSkinData(skinData);
                npc.removeForAll();
                npc.create();
                npc.spawnForAll();
                translator.translate("npc_skin_set_texturepack")
                        .replace("npc", npc.getData().getName())
                        .replace("asset", textureAsset)
                        .send(sender);
            } else {
                translator.translate("command_npc_modification_cancelled").send(sender);
            }
        } else if (isMirror) {
            if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.MIRROR_SKIN, true, sender).callEvent()) {
                npc.getData().setMirrorSkin(true);
                npc.removeForAll();
                npc.create();
                npc.spawnForAll();
                translator.translate("npc_skin_set_mirror").replace("npc", npc.getData().getName()).send(sender);
            } else {
                translator.translate("command_npc_modification_cancelled").send(sender);
            }
        } else if (isNone) {
            if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, false, sender).callEvent() && new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, null, sender).callEvent()) {
                npc.getData().setMirrorSkin(false);
                npc.getData().setSkinData(null);
                npc.removeForAll();
                npc.create();
                npc.spawnForAll();
                translator.translate("npc_skin_set_none").replace("npc", npc.getData().getName()).send(sender);
            } else {
                translator.translate("command_npc_modification_cancelled").send(sender);
            }
        } else try {
            SkinData.SkinVariant variant = slim ? SkinData.SkinVariant.SLIM : SkinData.SkinVariant.AUTO;
            SkinData skinData = FancyNpcs.getInstance().getSkinManagerImpl().getByIdentifier(skin, variant);
            skinData.setIdentifier(skin);

            if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, false, sender).callEvent() && new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, skinData, sender).callEvent()) {
                translator.translate("npc_skin_set")
                        .replace("npc", npc.getData().getName())
                        .replace("name", skinData.getIdentifier())
                        .send(sender);
                if (!skinData.hasTexture()) {
                    translator.translate("npc_skin_set_later").replace("npc", npc.getData().getName()).send(sender);
                }
                npc.getData().setMirrorSkin(false);
                npc.getData().setSkinData(skinData);
                npc.removeForAll();
                npc.create();
                npc.spawnForAll();

            } else {
                translator.translate("command_npc_modification_cancelled").send(sender);
            }
        } catch (final SkinLoadException e) {
            switch (e.getReason()) {
                case INVALID_URL -> translator.translate("npc_skin_failure_invalid_url").replace("npc", npc.getData().getName()).send(sender);
                case INVALID_FILE -> translator.translate("npc_skin_failure_invalid_file").replace("npc", npc.getData().getName()).send(sender);
                case INVALID_USERNAME -> translator.translate("npc_skin_failure_invalid_username").replace("npc", npc.getData().getName()).send(sender);
                case INVALID_PLACEHOLDER -> translator.translate("npc_skin_failure_invalid_placeholder").replace("npc", npc.getData().getName()).send(sender);
            }
        }
    }

    /* UTILITY METHODS */

    @Suggestions("SkinCMD/skin")
    public List<String> suggestSkin(final CommandContext<CommandSender> context, final CommandInput input) {
        return new ArrayList<>() {{
            add("@none");
            add("@mirror");
            add("@texturepack:"); // Resource pack texture skin (1.21.11+ Mannequin)
            Bukkit.getOnlinePlayers().stream().map(Player::getName).forEach(this::add);
            // Adding file names inside 'plugins/FancyNpcs/skins' to the list of completions.
            final File[] files = new File(FancyNpcs.getInstance().getDataFolder(), "skins").listFiles();
            if (files != null) {
                Arrays.stream(files).map(File::getName).filter(SkinUtils::isFile).forEach(this::add);
            }
        }};
    }

}
