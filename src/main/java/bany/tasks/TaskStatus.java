package bany.tasks;

/** Stores the display marker associated with a task's completion state. */
public enum TaskStatus {
    /** Task has been completed. */
    DONE("X"),
    /** Task has not been completed. */
    NOT_DONE("  ");

    /** Text displayed beside a task in the task list. */
    private final String status;

    /**
     * Creates a task status with its display marker.
     *
     * @param status marker used when displaying the status.
     */
    TaskStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the display marker for this status.
     *
     * @return status marker.
     */
    public String getStatus() {
        return status;
    }
}
