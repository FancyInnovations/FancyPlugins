package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.api.skins.SkinData;
import de.oliver.fancynpcs.api.skins.SkinLoadException;
import de.oliver.fancynpcs.skins.SkinUtils;
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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.oliver.fancynpcs.skins.SkinManagerImpl;
import de.oliver.fancynpcs.skins.mojang.MojangAPI;
import de.oliver.fancynpcs.skins.mineskin.RatelimitException;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public enum SkinCMD {
    INSTANCE; // SINGLETON

    private final Translator translator = FancyNpcs.getInstance().getTranslator();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    /* PARSERS AND SUGGESTIONS */

    @Command("npc skin <npc> <skin>")
    @Permission("fancynpcs.command.npc.skin")
    public void onSkin(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull @Argument(suggestions = "SkinCMD/skin") String skin,
            final @Flag("slim") boolean slim,
            final @Flag("static") boolean isStatic
    ) {
        if (npc.getData().getType() != EntityType.PLAYER) {
            translator.translate("command_unsupported_npc_type").send(sender);
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
            if (isStatic && SkinUtils.isUsername(skin)) {
                downloadStaticSkin(skin, sender, npc, slim);
                return;
            }

            final SkinData.SkinVariant variant = slim ? SkinData.SkinVariant.SLIM : SkinData.SkinVariant.AUTO;
            final SkinData skinData = FancyNpcs.getInstance().getSkinManagerImpl().getByIdentifier(skin, variant);
            skinData.setIdentifier(skin);

            applySkin(npc, skinData, sender);
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

    private void downloadStaticSkin(final String username, final CommandSender sender, final Npc npc, final boolean slim) {
        final SkinData.SkinVariant variant = slim ? SkinData.SkinVariant.SLIM : SkinData.SkinVariant.AUTO;

        try {
            final SkinData skinData = FancyNpcs.getInstance().getSkinManagerImpl().getByUsername(username, variant);
            if (skinData.hasTexture()) {
                processStaticSkin(skinData.getTextureValue(), username, sender, npc, slim);
                return;
            }
        } catch (final SkinLoadException e) {
            translator.translate("npc_skin_failure_invalid_username").replace("npc", npc.getData().getName()).send(sender);
            return;
        }

        CompletableFuture.supplyAsync(() -> {
            try {
                final UUID uuid = FancyNpcs.getInstance().getSkinManagerImpl().getUuidCache().getUUID(username);
                if (uuid == null) {
                    return null;
                }
                return new MojangAPI(SkinManagerImpl.EXECUTOR).fetchSkin(uuid.toString(), variant);
            } catch (final RatelimitException e) {
                return null;
            }
        }, SkinManagerImpl.EXECUTOR).thenAccept(fetchedSkinData -> {
            FancyNpcs.getInstance().getScheduler().runTask(null, () -> {
                if (fetchedSkinData == null || !fetchedSkinData.hasTexture()) {
                    translator.translate("npc_skin_failure_invalid_username").replace("npc", npc.getData().getName()).send(sender);
                    return;
                }
                processStaticSkin(fetchedSkinData.getTextureValue(), username, sender, npc, slim);
            });
        });
    }

    private void processStaticSkin(final String textureValue, final String username, final CommandSender sender, final Npc npc, final boolean slim) {
        try {
            final String decodedJson = new String(Base64.getDecoder().decode(textureValue));
            final JsonObject jsonObject = JsonParser.parseString(decodedJson).getAsJsonObject();
            final String skinUrl = jsonObject.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();

            final String fileName = username + ".png";
            final File skinFile = new File(new File(FancyNpcs.getInstance().getDataFolder(), "skins"), fileName);
            skinFile.getParentFile().mkdirs();

            httpClient.sendAsync(
                    HttpRequest.newBuilder().uri(URI.create(skinUrl)).GET().build(),
                    HttpResponse.BodyHandlers.ofFile(skinFile.toPath())
            ).thenAccept(response -> {
                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    FancyNpcs.getInstance().getScheduler().runTask(null, () -> {
                        try {
                            final SkinData.SkinVariant variant = slim ? SkinData.SkinVariant.SLIM : SkinData.SkinVariant.AUTO;
                            final SkinData skinData = FancyNpcs.getInstance().getSkinManagerImpl().getByIdentifier(fileName, variant);
                            skinData.setIdentifier(fileName);

                            if (!skinData.hasTexture()) {
                                translator.translate("npc_skin_set_later").replace("npc", npc.getData().getName()).send(sender);
                            }
                            applySkin(npc, skinData, sender);
                        } catch (final SkinLoadException e) {
                            translator.translate("npc_skin_failure_invalid_file").replace("npc", npc.getData().getName()).send(sender);
                        }
                    });
                } else {
                    FancyNpcs.getInstance().getScheduler().runTask(null, () -> {
                        translator.translate("npc_skin_failure_invalid_username").replace("npc", npc.getData().getName()).send(sender);
                    });
                }
            }).exceptionally(throwable -> {
                FancyNpcs.getInstance().getScheduler().runTask(null, () -> {
                    translator.translate("npc_skin_failure_invalid_username").replace("npc", npc.getData().getName()).send(sender);
                });
                return null;
            });
        } catch (final Exception e) {
            FancyNpcs.getInstance().getScheduler().runTask(null, () -> {
                translator.translate("npc_skin_failure_invalid_username").replace("npc", npc.getData().getName()).send(sender);
            });
        }
    }

    private void applySkin(final Npc npc, final SkinData skinData, final CommandSender sender) {
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, false, sender).callEvent() && new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.SKIN, skinData, sender).callEvent()) {
            translator.translate("npc_skin_set")
                    .replace("npc", npc.getData().getName())
                    .replace("name", skinData.getIdentifier())
                    .send(sender);
            if (!skinData.hasTexture() && !SkinUtils.isFile(skinData.getIdentifier())) {
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
    }

    @Suggestions("SkinCMD/skin")
    public List<String> suggestSkin(final CommandContext<CommandSender> context, final CommandInput input) {
        return new ArrayList<>() {{
            add("@none");
            add("@mirror");
            Bukkit.getOnlinePlayers().stream().map(Player::getName).forEach(this::add);
            // Adding file names inside 'plugins/FancyNpcs/skins' to the list of completions.
            final File[] files = new File(FancyNpcs.getInstance().getDataFolder(), "skins").listFiles();
            if (files != null) {
                Arrays.stream(files).map(File::getName).filter(SkinUtils::isFile).forEach(this::add);
            }
        }};
    }

}
