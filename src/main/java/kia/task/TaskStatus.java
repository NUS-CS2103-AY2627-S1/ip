package kia.task;

/**
 * Completion states for tasks.
 */
public enum TaskStatus {
    /** A task that has not been completed. */
    PENDING(" "),
    /** A task that has been completed. */
    DONE("X");

    private final String icon;

    /**
     * Creates a completion state with its display icon.
     *
     * @param icon the icon used when displaying this state
     */
    TaskStatus(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the display icon for this status.
     *
     * @return the status icon
     */
    public String getIcon() {
        return icon;
    }
}
