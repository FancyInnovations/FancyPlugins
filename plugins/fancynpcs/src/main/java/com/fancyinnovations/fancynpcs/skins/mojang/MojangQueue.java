package com.fancyinnovations.fancynpcs.skins.mojang;

import com.fancyinnovations.fancynpcs.FancyNpcs;
import com.fancyinnovations.fancynpcs.api.skins.SkinData;
import com.fancyinnovations.fancynpcs.api.skins.SkinGeneratedEvent;
import com.fancyinnovations.fancynpcs.skins.SkinGenerationQueue;
import com.fancyinnovations.fancynpcs.skins.SkinGenerationRequest;
import com.fancyinnovations.fancynpcs.skins.SkinManagerImpl;
import com.fancyinnovations.fancynpcs.skins.mineskin.RatelimitException;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MojangQueue implements SkinGenerationQueue {

    private static MojangQueue INSTANCE;

    private final Queue<SkinGenerationRequest> queue;
    private final MojangAPI api;
    private ScheduledFuture<?> scheduler;
    private long nextRequestTime = System.currentTimeMillis();

    private MojangQueue() {
        this.queue = new LinkedList<>();
        this.api = new MojangAPI(SkinManagerImpl.EXECUTOR);

        run();
    }

    public static MojangQueue get() {
        if (INSTANCE == null) {
            INSTANCE = new MojangQueue();
        }

        return INSTANCE;
    }

    @Override
    public void run() {
        scheduler = SkinManagerImpl.EXECUTOR.scheduleWithFixedDelay(this::pollMany, 5, 1, TimeUnit.SECONDS);
    }

    private void pollMany() {
        for (int i = 0; i < 5; i++) {
            poll();
        }
    }

    private void poll() {
        if (this.queue.isEmpty()) {
            return;
        }

        if (System.currentTimeMillis() < this.nextRequestTime) {
            FancyNpcs.getInstance().getFancyLogger().debug("Retrying to fetch skin from Mojang in " + (nextRequestTime - System.currentTimeMillis()) + "ms");
            return;
        }

        SkinGenerationRequest req = this.queue.poll();
        if (req == null) {
            return;
        }

        try {
            FancyNpcs.getInstance().getFancyLogger().debug("Fetching skin from Mojang: " + req.getID());
            SkinData skinData = this.api.fetchSkin(req.getID(), req.getVariant());
            new SkinGeneratedEvent(req.getID(), skinData).callEvent();
        } catch (RatelimitException e) {
            this.nextRequestTime = e.getNextRequestTime();
            this.queue.add(req);
            FancyNpcs.getInstance().getFancyLogger().debug("Failed to generate skin: ratelimited by Mojang, retrying in " + (nextRequestTime - System.currentTimeMillis()) + "ms");
            return;
        }

        this.nextRequestTime = System.currentTimeMillis();
    }

    @Override
    public void add(SkinGenerationRequest req) {
        // check if request is already in queue
        for (SkinGenerationRequest r : this.queue) {
            if (r.getID().equals(req.getID())) {
                return;
            }
        }

        this.queue.add(req);
    }

    @Override
    public void clear() {
        this.queue.clear();
    }

    @Override
    public ScheduledFuture<?> getScheduler() {
        return scheduler;
    }

}
