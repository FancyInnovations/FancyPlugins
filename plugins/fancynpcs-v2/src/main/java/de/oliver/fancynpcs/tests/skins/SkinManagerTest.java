package de.oliver.fancynpcs.tests.skins;

import de.oliver.fancynpcs.api.skins.SkinData;
import de.oliver.fancynpcs.api.skins.SkinLoadException;
import de.oliver.fancynpcs.skins.SkinGenerationRequest;
import de.oliver.fancynpcs.skins.SkinManagerImpl;
import de.oliver.fancynpcs.skins.cache.SkinCache;
import de.oliver.fancynpcs.skins.cache.SkinCacheData;
import de.oliver.fancynpcs.skins.cache.SkinCacheFile;
import de.oliver.fancynpcs.skins.cache.SkinCacheMemory;
import de.oliver.fancynpcs.skins.uuidcache.UUIDMemoryCache;
import de.oliver.plugintests.annotations.FPAfterEach;
import de.oliver.plugintests.annotations.FPBeforeEach;
import de.oliver.plugintests.annotations.FPTest;
import org.bukkit.entity.Player;

import java.util.UUID;

import static de.oliver.plugintests.Expectable.expect;

public class SkinManagerTest {

    private SkinManagerImpl manager;
    private SkinCache memCache;
    private SkinCache fileCache;
    private FakeSkinQueue mojangQueue;
    private FakeSkinQueue mineSkinQueue;

    @FPBeforeEach
    public void setUp(Player player) {
        memCache = new SkinCacheMemory();
        fileCache = new SkinCacheFile();
        mojangQueue = new FakeSkinQueue();
        mineSkinQueue = new FakeSkinQueue();
        manager = new SkinManagerImpl(new UUIDMemoryCache(), fileCache, memCache, mojangQueue, mineSkinQueue);
    }

    @FPAfterEach
    public void tearDown(Player player) {
        memCache.clear();
        fileCache.clear();
    }

    @FPTest(name = "SkinManagerImpl#cacheSkin")
    public void testCacheSkin(Player player) {
        SkinData data = new SkinData("TestSkin", SkinData.SkinVariant.SLIM, "TestSignature", "TestTexture");
        manager.cacheSkin(data);

        SkinCacheData gotFromFile = fileCache.getSkin("TestSkin");
        expect(gotFromFile).toBeDefined();
        expect(gotFromFile.skinData()).toEqual(data);

        SkinCacheData gotFromMem = memCache.getSkin("TestSkin");
        expect(gotFromMem).toBeDefined();
        expect(gotFromMem.skinData()).toEqual(data);
    }

    @FPTest(name = "SkinManagerImpl#getByUUID")
    public void testGetByUUID(Player player) {
        record TestCase(
                String name,
                UUID uuid,
                SkinData.SkinVariant variant,
                SkinData cachedFile,
                SkinData cachedMem,
                SkinData expData,
                boolean expQueued,
                SkinGenerationRequest mojangRequest
        ) {
        }

        TestCase[] testCases = {
                new TestCase(
                        "Skin is cached in file",
                        UUID.fromString("12345678-1234-1234-1234-123456789012"),
                        SkinData.SkinVariant.SLIM,
                        new SkinData("12345678-1234-1234-1234-123456789012", SkinData.SkinVariant.SLIM, "TestSignatureFile", "TestTextureFile"),
                        null,
                        new SkinData("12345678-1234-1234-1234-123456789012", SkinData.SkinVariant.SLIM, "TestSignatureFile", "TestTextureFile"),
                        false,
                        null
                ),
                new TestCase(
                        "Skin is cached in memory",
                        UUID.fromString("12345678-1234-1234-1234-123456789012"),
                        SkinData.SkinVariant.SLIM,
                        null,
                        new SkinData("12345678-1234-1234-1234-123456789012", SkinData.SkinVariant.SLIM, "TestSignatureMem", "TestTextureMem"),
                        new SkinData("12345678-1234-1234-1234-123456789012", SkinData.SkinVariant.SLIM, "TestSignatureMem", "TestTextureMem"),
                        false,
                        null
                ),
                new TestCase(
                        "Skin is not cached",
                        UUID.fromString("12345678-1234-1234-1234-123456789012"),
                        SkinData.SkinVariant.SLIM,
                        null,
                        null,
                        new SkinData("12345678-1234-1234-1234-123456789012", SkinData.SkinVariant.SLIM, null, null),
                        true,
                        new SkinGenerationRequest("12345678-1234-1234-1234-123456789012", SkinData.SkinVariant.SLIM)
                )
        };

        for (TestCase testCase : testCases) {
            setUp(player);

            System.out.println("Running test case: " + testCase.name);

            if (testCase.cachedFile != null) {
                fileCache.addSkin(testCase.cachedFile);
            }
            if (testCase.cachedMem != null) {
                memCache.addSkin(testCase.cachedMem);
            }

            SkinData got = manager.getByUUID(testCase.uuid, testCase.variant);
            expect(got).toEqual(testCase.expData);

            if (testCase.expQueued) {
                expect(mojangQueue.getQueue().size()).toBe(1);
                SkinGenerationRequest queued = mojangQueue.getQueue().getFirst();
                expect(queued).toEqual(testCase.mojangRequest);
            } else {
                expect(mojangQueue.getQueue().isEmpty()).toBe(true);
            }

            tearDown(player);
        }
    }

    @FPTest(name = "SkinManagerImpl#getByUsername valid")
    public void testGetByUsernameValid(Player player) {
        String username = "OliverHD";
        SkinData.SkinVariant variant = SkinData.SkinVariant.SLIM;

        SkinData got = manager.getByUsername(username, variant);
        expect(got).toBeDefined();
        expect(got.getIdentifier()).toEqual(username);
        expect(got.getVariant()).toEqual(variant);

        expect(mojangQueue.getQueue().size()).toBe(1);
        SkinGenerationRequest queued = mojangQueue.getQueue().getFirst();
        expect(queued).toBeDefined();
        expect(queued.getID()).toHaveLength(UUID.randomUUID().toString().length());
        expect(queued.getVariant()).toEqual(variant);
    }

    @FPTest(name = "SkinManagerImpl#getByUsername invalid")
    public void testGetByUsernameInvalid(Player player) {
        String username = "._.";
        SkinData.SkinVariant variant = SkinData.SkinVariant.SLIM;

        Runnable runnable = () -> manager.getByUsername(username, variant);
        SkinLoadException ex = expect(runnable).toThrow(SkinLoadException.class);

        expect(ex).toBeDefined();
        expect(ex.getReason()).toBe(SkinLoadException.Reason.INVALID_USERNAME);
    }

    @FPTest(name = "SkinManagerImpl#getByURL valid")
    public void testGetByURLValid(Player player) {
        String url = "https://example.com/skin.png";
        SkinData.SkinVariant variant = SkinData.SkinVariant.SLIM;

        SkinData got = manager.getByURL(url, variant);
        expect(got).toBeDefined();
        expect(got.getIdentifier()).toEqual(url);
        expect(got.getVariant()).toEqual(variant);

        expect(mineSkinQueue.getQueue().size()).toBe(1);
        SkinGenerationRequest queued = mineSkinQueue.getQueue().getFirst();
        expect(queued).toBeDefined();
        expect(queued.getID()).toEqual(url);
        expect(queued.getVariant()).toEqual(variant);
        expect(queued.getMineskinRequest()).toBeDefined();
    }

    @FPTest(name = "SkinManagerImpl#getByURL invalid")
    public void testGetByURLInvalid(Player player) {
        String url = "invalid-url!";
        SkinData.SkinVariant variant = SkinData.SkinVariant.SLIM;

        Runnable runnable = () -> manager.getByURL(url, variant);
        SkinLoadException ex = expect(runnable).toThrow(SkinLoadException.class);

        expect(ex).toBeDefined();
        expect(ex.getReason()).toBe(SkinLoadException.Reason.INVALID_URL);
    }

    @FPTest(name = "SkinManagerImpl#getByFile invalid")
    public void testGetByFileInvalid(Player player) {
        String filePath = "invalid-file-path-" + System.currentTimeMillis() + ".html";
        SkinData.SkinVariant variant = SkinData.SkinVariant.SLIM;

        Runnable runnable = () -> manager.getByFile(filePath, variant);
        SkinLoadException ex = expect(runnable).toThrow(SkinLoadException.class);

        expect(ex).toBeDefined();
        expect(ex.getReason()).toBe(SkinLoadException.Reason.INVALID_FILE);
    }
}
