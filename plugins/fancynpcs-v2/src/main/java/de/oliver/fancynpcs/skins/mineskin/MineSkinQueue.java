package de.oliver.fancynpcs.skins.mineskin;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.skins.SkinData;
import de.oliver.fancynpcs.api.skins.SkinGeneratedEvent;
import de.oliver.fancynpcs.skins.SkinGenerationQueue;
import de.oliver.fancynpcs.skins.SkinGenerationRequest;
import de.oliver.fancynpcs.skins.SkinManagerImpl;
import org.mineskin.data.SkinInfo;
import org.mineskin.data.Variant;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MineSkinQueue implements SkinGenerationQueue {
    private static MineSkinQueue INSTANCE;

    private final MineSkinAPI api;
    private final Queue<SkinGenerationRequest> queue;
    private ScheduledFuture<?> scheduler;

    private long nextRequestTime = System.currentTimeMillis();

    private MineSkinQueue() {
        this.queue = new LinkedList<>();
        this.api = new MineSkinAPI(SkinManagerImpl.EXECUTOR);

        run();
    }

    public static MineSkinQueue get() {
        if (INSTANCE == null) {
            INSTANCE = new MineSkinQueue();
        }

        return INSTANCE;
    }

    @Override
    public void run() {
        scheduler = SkinManagerImpl.EXECUTOR.scheduleWithFixedDelay(this::poll, 5, 1, TimeUnit.SECONDS);
    }

    private void poll() {
        if (this.queue.isEmpty()) {
            return;
        }

        if (System.currentTimeMillis() < this.nextRequestTime) {
            FancyNpcs.getInstance().getFancyLogger().debug("Retrying to generate skin in " + (nextRequestTime - System.currentTimeMillis()) + "ms");
            return;
        }

        SkinGenerationRequest req = this.queue.poll();
        if (req == null) {
            return;
        }

        try {
            FancyNpcs.getInstance().getFancyLogger().debug("Fetching skin from MineSkin: " + req.getID());
            SkinInfo skin = this.api.generateSkin(req.getMineskinRequest());
            if (skin == null) {
                this.nextRequestTime = System.currentTimeMillis();
                return;
            }

            SkinData skinData = new SkinData(
                    req.getID(),
                    skin.variant() == Variant.SLIM ? SkinData.SkinVariant.SLIM : SkinData.SkinVariant.AUTO,
                    skin.texture().data().value(),
                    skin.texture().data().signature()
            );
            new SkinGeneratedEvent(req.getID(), skinData).callEvent();
        } catch (RatelimitException e) {
            this.nextRequestTime = e.getNextRequestTime();
            this.queue.add(req);
            FancyNpcs.getInstance().getFancyLogger().debug("Failed to generate skin: ratelimited by MineSkin, retrying in " + (nextRequestTime - System.currentTimeMillis()) + "ms");
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
