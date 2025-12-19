package de.oliver.fancynpcs.skins;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.oliver.fancylib.UUIDFetcher;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.skins.SkinData;
import de.oliver.fancynpcs.api.skins.SkinGeneratedEvent;
import de.oliver.fancynpcs.api.skins.SkinLoadException;
import de.oliver.fancynpcs.api.skins.SkinManager;
import de.oliver.fancynpcs.skins.cache.SkinCache;
import de.oliver.fancynpcs.skins.cache.SkinCacheData;
import de.oliver.fancynpcs.skins.uuidcache.UUIDCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.lushplugins.chatcolorhandler.ChatColorHandler;
import org.mineskin.data.Variant;
import org.mineskin.request.GenerateRequest;

import java.io.File;
import java.net.MalformedURLException;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SkinManagerImpl implements SkinManager, Listener {

    public final static ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(
            5,
            new ThreadFactoryBuilder()
                    .setNameFormat("FancyNpcs-Skins-%d")
                    .build()
    );

    private final String SKINS_DIRECTORY = "plugins/FancyNpcs/skins/";

    private final UUIDCache uuidCache;
    private final SkinCache fileCache;
    private final SkinCache memCache;
    private final SkinGenerationQueue mojangQueue;
    private final SkinGenerationQueue mineSkinQueue;

    public SkinManagerImpl(
            UUIDCache uuidCache,
            SkinCache fileCache,
            SkinCache memCache,
            SkinGenerationQueue mojangQueue,
            SkinGenerationQueue mineSkinQueue
    ) {
        this.uuidCache = uuidCache;
        this.fileCache = fileCache;
        this.memCache = memCache;
        this.mojangQueue = mojangQueue;
        this.mineSkinQueue = mineSkinQueue;

        File skinsDir = new File(SKINS_DIRECTORY);
        if (!skinsDir.exists()) {
            skinsDir.mkdirs();
        }
    }

    @Override
    public SkinData getByIdentifier(String identifier, SkinData.SkinVariant variant) throws SkinLoadException {
        if (SkinUtils.isUUID(identifier)) {
            return getByUUID(UUID.fromString(identifier), variant);
        }

        if (SkinUtils.isURL(identifier)) {
            return getByURL(identifier, variant);
        }

        if (SkinUtils.isFile(identifier)) {
            return getByFile(identifier, variant);
        }

        if (SkinUtils.isPlaceholder(identifier)) {
            String parsed = ChatColorHandler.translate(identifier);

            if (parsed.isBlank() || parsed.equalsIgnoreCase("null") || SkinUtils.isPlaceholder(parsed)) {
                throw new SkinLoadException(SkinLoadException.Reason.INVALID_PLACEHOLDER, "(RAW = '" + identifier + "'; PARSED = '" + parsed + "')");
            }

            return getByIdentifier(parsed, variant);
        }

        return getByUsername(identifier, variant);
    }

    @Override
    public SkinData getByUUID(UUID uuid, SkinData.SkinVariant variant) {
        SkinData cached = tryToGetFromCache(uuid.toString(), variant);
        if (cached != null) {
            return cached;
        }

        mojangQueue.add(new SkinGenerationRequest(uuid.toString(), variant));

//        GenerateRequest genReq = GenerateRequest.user(uuid);
//        genReq.variant(Variant.valueOf(variant.name()));
//        mineSkinQueue.add(new MineSkinQueue.SkinRequest(uuid.toString(), genReq));
        return new SkinData(uuid.toString(), variant);
    }

    @Override
    public SkinData getByUsername(String username, SkinData.SkinVariant variant) throws SkinLoadException {
        SkinData cached = tryToGetFromCache(username, variant);
        if (cached != null) {
            return cached;
        }

        UUID uuid = uuidCache.getUUID(username);
        if (uuid == null) {
            uuid = UUIDFetcher.getUUID(username);
            if (uuid == null) {
                throw new SkinLoadException(SkinLoadException.Reason.INVALID_USERNAME, "(USERNAME = '" + username + "')");
            }
            uuidCache.cacheUUID(username, uuid);
        }

        SkinData dataByUUID = getByUUID(uuid, variant);

        return new SkinData(username, dataByUUID.getVariant(), dataByUUID.getTextureValue(), dataByUUID.getTextureSignature());
    }

    @Override
    public SkinData getByURL(String url, SkinData.SkinVariant variant) throws SkinLoadException {
        SkinData cached = tryToGetFromCache(url, variant);
        if (cached != null) {
            return cached;
        }

        GenerateRequest genReq;
        try {
            genReq = GenerateRequest.url(url);
        } catch (final IllegalArgumentException | MalformedURLException e) {
            throw new SkinLoadException(SkinLoadException.Reason.INVALID_URL, "(URL = '" + url + "')");
        }
        genReq.variant(Variant.valueOf(variant.name()));
        mineSkinQueue.add(new SkinGenerationRequest(url, variant, genReq));
        return new SkinData(url, variant);
    }

    @Override
    public SkinData getByFile(String filePath, SkinData.SkinVariant variant) throws SkinLoadException {
        SkinData cached = tryToGetFromCache(filePath, variant);
        if (cached != null) {
            return cached;
        }

        File file = new File(SKINS_DIRECTORY + filePath);
        if (!file.exists()) {
            throw new SkinLoadException(SkinLoadException.Reason.INVALID_FILE, "(FILE = '" + filePath + "')");
        }

        GenerateRequest genReq = GenerateRequest.upload(file);
        genReq.variant(Variant.valueOf(variant.name()));
        mineSkinQueue.add(new SkinGenerationRequest(filePath, variant, genReq));
        return new SkinData(filePath, variant);
    }

    @EventHandler
    public void onSkinGenerated(SkinGeneratedEvent event) {
        if (event.getSkin() == null || !event.getSkin().hasTexture()) {
            FancyNpcs.getInstance().getFancyLogger().error("Generated skin has no texture!");
            return;
        }

        for (Npc npc : FancyNpcs.getInstance().getNpcManager().getAllNpcs()) {
            SkinData skin = npc.getData().getSkinData();
            if (skin == null)
                continue;

            String id = skin.getParsedIdentifier();
            if (SkinUtils.isUsername(id)) {
                UUID uuid = uuidCache.getUUID(id);
                if (uuid == null) {
                    uuid = UUIDFetcher.getUUID(id);
                }

                if (uuid != null) {
                    uuidCache.cacheUUID(id, uuid);
                    id = uuid.toString();
                }
            }
            if (id.equals(event.getId())) {
                final SkinData updatedSkin = new SkinData(
                        skin.getIdentifier(),
                        event.getSkin().getVariant(),
                        event.getSkin().getTextureValue(),
                        event.getSkin().getTextureSignature()
                );
                npc.getData().setSkinData(updatedSkin);
                npc.removeForAll();
                npc.spawnForAll();
                FancyNpcs.getInstance().getFancyLogger().info("Updated skin for NPC: " + npc.getData().getName());
            }
        }

        cacheSkin(event.getSkin());
    }

    private SkinData tryToGetFromCache(String identifier, SkinData.SkinVariant variant) {
        FancyNpcs.getInstance().getFancyLogger().debug("Trying to get skin from mem cache: " + identifier);

        SkinCacheData data = memCache.getSkin(identifier);
        if (data != null) {
            if (data.skinData().getVariant() != variant) {
                FancyNpcs.getInstance().getFancyLogger().debug("Skin variant does not match: " + identifier);
                return null;
            }

            FancyNpcs.getInstance().getFancyLogger().debug("Found skin from mem cache: " + identifier);
            return data.skinData();
        }

        FancyNpcs.getInstance().getFancyLogger().debug("Trying to get skin from file cache: " + identifier);

        data = fileCache.getSkin(identifier);
        if (data != null) {
            if (data.skinData().getVariant() != variant) {
                FancyNpcs.getInstance().getFancyLogger().debug("Skin variant does not match: " + identifier);
                return null;
            }

            FancyNpcs.getInstance().getFancyLogger().debug("Found skin from file cache: " + identifier);
            memCache.addSkin(data.skinData());
            return data.skinData();
        }

        FancyNpcs.getInstance().getFancyLogger().debug("Skin not found in cache: " + identifier);
        return null;
    }

    public void cacheSkin(SkinData skinData) {
        memCache.addSkin(skinData);
        fileCache.addSkin(skinData);
    }

    public UUIDCache getUuidCache() {
        return uuidCache;
    }

    public SkinCache getFileCache() {
        return fileCache;
    }

    public SkinCache getMemCache() {
        return memCache;
    }
}
