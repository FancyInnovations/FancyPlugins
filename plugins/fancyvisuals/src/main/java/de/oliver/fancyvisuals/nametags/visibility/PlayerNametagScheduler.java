package de.oliver.fancyvisuals.nametags.visibility;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.oliver.fancyvisuals.utils.distributedWorkload.DistributedWorkload;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayerNametagScheduler {

    /**
     * ScheduledExecutorService instance responsible for scheduling periodic execution of
     * the DistributedWorkload<PlayerNametag>. It manages the timing and frequency
     * of workload distribution, ensuring that tasks are run at fixed intervals.
     */
    private final ScheduledExecutorService schedulerExecutor;

    /**
     * DistributedWorkload instance responsible for managing and executing tasks related
     * to PlayerNametag objects. It divides the tasks across multiple buckets and performs
     * specified actions on each element. Actions include updating visibility and checking
     * whether a PlayerNametag needs to be updated.
     */
    private final DistributedWorkload<PlayerNametag> workload;

    private final long updateIntervalMs;

    public PlayerNametagScheduler(ExecutorService workerExecutor, int bucketSize, long updateIntervalMs) {
        this.schedulerExecutor = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder()
                        .setNameFormat("PlayerNametagScheduler")
                        .build()
        );
        this.updateIntervalMs = updateIntervalMs;

        this.workload = new DistributedWorkload<>(
                "PlayerNametagWorkload",
                PlayerNametag::updateVisibilityForAll,
                (nt) -> !nt.getPlayer().isOnline(),
                bucketSize,
                workerExecutor
        );
    }

    /**
     * Initializes the PlayerNametagScheduler and starts the periodic execution
     * of the DistributedWorkload<PlayerNametag>. The workload is scheduled to
     * run at a fixed rate with an initial delay of 0 seconds and a period of
     * 25 seconds between subsequent executions.
     */
    public void init() {
        schedulerExecutor.scheduleWithFixedDelay(workload, updateIntervalMs, updateIntervalMs, TimeUnit.MILLISECONDS);
    }

    public void add(PlayerNametag nametag) {
        workload.addValue(() -> nametag);
    }

    public void shutdown() {
        schedulerExecutor.shutdownNow();
    }
}
