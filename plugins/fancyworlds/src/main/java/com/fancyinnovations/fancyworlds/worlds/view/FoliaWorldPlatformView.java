package com.fancyinnovations.fancyworlds.worlds.view;

import com.fancyinnovations.fancyworlds.main.FancyWorldsPlugin;
import io.papermc.paper.FeatureHooks;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class FoliaWorldPlatformView extends PaperWorldPlatformView {

    public FoliaWorldPlatformView(FancyWorldsPlugin plugin) {
        super(plugin);
    }

    @Override
    protected CompletableFuture<Void> saveAsync(World world, boolean flush) {
        final var futures = new ArrayList<CompletableFuture<Void>>();
        final var level = ((CraftWorld) world).getHandle();

        level.regioniser.computeForAllRegionsUnsynchronised(region -> {
            final var future = new CompletableFuture<Void>();
            futures.add(future);

            final var centerChunk = region.getCenterChunk();
            if (centerChunk == null) {
                future.complete(null);
                return;
            }

            plugin().getServer().getRegionScheduler().run(plugin(), world, centerChunk.x, centerChunk.z, task -> {
                try {
                    level.getChunkSource().save(flush);
                    future.complete(null);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            });
        });

        futures.add(saveLevelDataAsync(world));
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public CompletableFuture<Boolean> unloadWorld(World world, boolean save) {
        final var handle = ((CraftWorld) world).getHandle();
        final var server = (CraftServer) plugin().getServer();

        if (server.getServer().getLevel(handle.dimension()) == null) {
            return CompletableFuture.completedFuture(false);
        }
        if (handle.dimension() == net.minecraft.world.level.Level.OVERWORLD) {
            return CompletableFuture.completedFuture(false);
        }
        if (!handle.players().isEmpty()) {
            return CompletableFuture.completedFuture(false);
        }

        return plugin().supplyGlobal(() -> CompletableFuture.completedFuture(new WorldUnloadEvent(handle.getWorld()).callEvent()))
                .thenCompose(success -> {
                    if (!success) {
                        return CompletableFuture.completedFuture(false);
                    }

                    final var saving = save ? saveAsync(world, true) : CompletableFuture.completedFuture(null);
                    return saving.handle((result, throwable) -> plugin().supplyGlobal(() -> {
                        try {
                            handle.getChunkSource().close(false);
                            FeatureHooks.closeEntityManager(handle, save);
                            handle.levelStorageAccess.close();
                        } catch (Exception ignored) {
                        }
                        return CompletableFuture.completedFuture(null);
                    })).thenCompose(self -> self).thenApply(ignored -> {
                        try {
                            final var field = server.getClass().getDeclaredField("worlds");
                            field.trySetAccessible();
                            @SuppressWarnings("unchecked")
                            final var worlds = (Map<String, World>) field.get(server);
                            worlds.remove(world.getName().toLowerCase(Locale.ROOT));
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            return false;
                        }

                        server.getServer().removeLevel(handle);
                        handle.regioniser.computeForAllRegionsUnsynchronised(regionThread -> {
                            if (regionThread.getData().world != handle) {
                                return;
                            }
                            regionThread.getData().getRegionSchedulingHandle().markNonSchedulable();
                        });

                        return true;
                    });
                });
    }

    private FancyWorldsPlugin plugin() {
        return FancyWorldsPlugin.get();
    }
}
