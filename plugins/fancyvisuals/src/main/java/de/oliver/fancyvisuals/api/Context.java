package de.oliver.fancyvisuals.api;

/**
 * Enum representing different contexts in which operations can be performed.
 * Each context is associated with a specific priority level that indicates its specificity.
 */
public enum Context {

    SERVER(1),
    WORLD(2),
    GROUP(3),
    PLAYER(4),
    ;

    private final int priority;

    Context(int priority) {
        this.priority = priority;
    }

    public String getName() {
        return name().toLowerCase();
    }

    /**
     * Retrieves the priority level associated with this context.
     * Higher priority levels indicate more specific contexts.
     *
     * @return the priority level as an integer
     */
    public int getPriority() {
        return priority;
    }
}
