package de.oliver.fancynpcs.skins;

import java.util.concurrent.ScheduledFuture;

public interface SkinGenerationQueue {

    void run();

    void add(SkinGenerationRequest request);

    void clear();

    ScheduledFuture<?> getScheduler();
}
