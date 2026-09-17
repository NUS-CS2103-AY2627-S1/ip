package megatron.task;

/**
 * Represents the completion status of a task.
 */
public enum TaskStatus {
    /**
     * A task that is not done.
     */
    NOT_DONE("[ ]"),
    /**
     * A task that is done.
     */
    DONE("[X]");

    private final String marker;

    /**
     * Creates a task status with its user interface marker.
     */
    TaskStatus(String marker) {
        this.marker = marker;
    }

    /**
     * Returns the status marker shown in the user interface.
     *
     * @return The status marker.
     */
    public String getMarker() {
        return marker;
    }
}
