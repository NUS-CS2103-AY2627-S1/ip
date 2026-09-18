package rene.task;

/**
 * Represents whether a task has been completed.
 */
public enum TaskStatus {
    /** Indicates that a task has been completed. */
    DONE("X"),
    /** Indicates that a task has not been completed. */
    NOT_DONE(" ");

    private final String icon;

    /**
     * Creates a status with the icon used in task displays.
     *
     * @param icon the display icon for this status.
     */
    TaskStatus(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the icon used to display this status.
     *
     * @return the status icon.
     */
    public String getIcon() {
        return icon;
    }
}
