package megatron.task;

/**
 * Identifies the kind of task stored in Megatron.
 */
public enum TaskType {
    /**
     * A task without a date or time.
     */
    TODO("[T]"),
    /**
     * A task with a deadline.
     */
    DEADLINE("[D]"),
    /**
     * A task with a start and end.
     */
    EVENT("[E]"),
    /**
     * Marker used by the base Task class when it is instantiated directly.
     */
    TASK("[-]");

    private final String marker;

    /**
     * Creates a task type with its user interface marker.
     */
    TaskType(String marker) {
        this.marker = marker;
    }

    /**
     * Returns the marker shown before a task in the user interface.
     *
     * @return The task type marker.
     */
    public String getMarker() {
        return marker;
    }
}
