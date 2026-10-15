package nova.task;

/**
 * Represents the completion state of a task.
 */
public enum TaskStatus {
    /** A task that has not been completed. */
    NOT_DONE(" "),

    /** A task that has been completed. */
    DONE("X");

    private final String displayIcon;

    /**
     * Creates a task status with its display marker.
     *
     * @param displayIcon the marker displayed in a task's status box
     */
    TaskStatus(String displayIcon) {
        this.displayIcon = displayIcon;
    }

    /**
     * Returns the marker used when displaying this status.
     *
     * @return the display marker
     */
    public String getDisplayIcon() {
        return displayIcon;
    }
}
