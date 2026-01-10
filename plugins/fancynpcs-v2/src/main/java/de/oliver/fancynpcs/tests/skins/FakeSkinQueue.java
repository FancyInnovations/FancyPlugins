package de.oliver.fancynpcs.tests.skins;

import de.oliver.fancynpcs.skins.SkinGenerationQueue;
import de.oliver.fancynpcs.skins.SkinGenerationRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

public class FakeSkinQueue implements SkinGenerationQueue {

    private final List<SkinGenerationRequest> queue;
    private boolean running;

    public FakeSkinQueue() {
        this.running = false;
        this.queue = new ArrayList<>();
    }

    @Override
    public void run() {
        this.running = true;
    }

    @Override
    public void add(SkinGenerationRequest request) {
        this.queue.add(request);
    }

    @Override
    public void clear() {
        this.queue.clear();
    }

    @Override
    public ScheduledFuture<?> getScheduler() {
        return null;
    }

    public boolean isRunning() {
        return running;
    }

    public List<SkinGenerationRequest> getQueue() {
        return queue;
    }
}
