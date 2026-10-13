package de.oliver.fancyvisuals.utils.distributedWorkload;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A Bucket provides storage for entries, which are suppliers that yield elements of the
 * specified type. It allows adding entries and performing actions on them, with optional
 * asynchronous execution.
 *
 * @param <T> The type of the elements in the bucket
 */
public class Bucket<T> {

    private final String name;
    private final LinkedList<Supplier<T>> entries;

    /**
     * Constructs an empty Bucket.
     */
    public Bucket(String name) {
        this.name = name;
        this.entries = new LinkedList<>();
    }

    /**
     * Adds a new entry to the bucket.
     *
     * @param entry the supplier providing the entry to be added to the bucket
     */
    public void addEntry(Supplier<T> entry) {
        entries.add(entry);
    }

    /**
     * Executes an action for each entry in the bucket. If the action is set to be
     * executed asynchronously, it will run in separate threads; otherwise, it will
     * execute in the current thread.
     *
     * @param action   the action to be performed on each entry
     * @param escape   a condition to determine which entries should be removed after the action is performed
     * @param executor the executor service to be used for asynchronous execution
     */
    public void executeAction(Consumer<T> action, Predicate<T> escape, ExecutorService executor) {
        LinkedList<Supplier<T>> suppliers = new LinkedList<>(entries);

        for (Supplier<T> supplier : suppliers) {
            executor.submit(() -> action.accept(supplier.get()));
        }

        entries.removeIf(s -> escape.test(s.get()));
    }

    public int size() {
        return entries.size();
    }

    public String getName() {
        return name;
    }
}
