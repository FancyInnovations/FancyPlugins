package de.oliver.fancyvisuals.utils.distributedWorkload;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * DistributedWorkload is a class that manages and executes a workload distributed across multiple buckets.
 * Each bucket contains a subset of the workload and executes a specified action on its elements.
 *
 * @param <T> The type of the elements in the workload
 */
public class DistributedWorkload<T> implements Runnable {

    private final String workloadName;
    private final Consumer<T> action;
    private final Predicate<T> escapeCondition;
    private final int bucketSize;
    private final ExecutorService executorService;

    private final List<Bucket<T>> buckets;
    private int currentBucket;

    /**
     * Creates a new DistributedWorkload instance.
     *
     * @param workloadName    the name of the workload
     * @param action          the action to be performed on each element of the workload
     * @param escapeCondition a condition to determine which elements should be removed after the action is performed
     * @param bucketSize      the number of buckets into which the workload will be split
     * @param executorService the executor service to be used for asynchronous execution
     */
    public DistributedWorkload(String workloadName, Consumer<T> action, Predicate<T> escapeCondition, int bucketSize, ExecutorService executorService) {
        this.workloadName = workloadName;
        this.action = action;
        this.escapeCondition = escapeCondition;
        this.bucketSize = bucketSize;
        this.executorService = executorService;

        this.currentBucket = 0;
        this.buckets = new ArrayList<>(bucketSize);
        for (int i = 0; i < bucketSize; i++) {
            this.buckets.add(new Bucket<>("DWL-" + workloadName + "-" + i));
        }
    }

    /**
     * Executes the next bucket of workload by invoking the runNextBucket method.
     * This method is called when this instance is run as a Runnable.
     * Each bucket contains a portion of the workload and executes the specified action
     * on each of its elements, either synchronously or asynchronously,
     * depending on the configuration of the DistributedWorkload.
     */
    @Override
    public void run() {
        runNextBucket();
    }

    /**
     * Adds a new value to the smallest bucket within the distributed workload.
     * The value is supplied by the specified Supplier.
     *
     * @param valueSupplier the supplier providing the value to be added to the bucket
     */
    public void addValue(Supplier<T> valueSupplier) {
        Bucket<T> smallestBucket = buckets.getFirst();

        for (int i = 1; i < bucketSize; i++) {
            if (smallestBucket.size() == 0) {
                break;
            }

            if (buckets.get(i).size() < smallestBucket.size()) {
                smallestBucket = buckets.get(i);
            }
        }

        smallestBucket.addEntry(valueSupplier);

    }

    /**
     * Advances to the next bucket in the list and executes its action.
     * If the current bucket is the last one in the list, it wraps around to the first bucket.
     * The action is executed on each element of the current bucket according to the configured
     * conditions and can be run asynchronously if specified.
     */
    private void runNextBucket() {
        currentBucket++;
        if (currentBucket >= buckets.size()) {
            currentBucket = 0;
        }

        Bucket<T> bucket = buckets.get(currentBucket);
        bucket.executeAction(action, escapeCondition, executorService);
    }
}
