package edith.task;

/**
 * Represents the fixed completion states that a task can have.
 */
public enum TaskStatus {
    /** A task that has not yet been completed. */
    NOT_DONE(" "),

    /** A task that has been completed. */
    DONE("X");

    /** The icon shown for this status in Edith's task list. */
    private final String icon;

    /**
     * Creates a task status with its display icon.
     *
     * @param icon the icon shown for this status
     */
    TaskStatus(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the icon used to display this status.
     *
     * @return the status icon
     */
    public String getIcon() {
        return icon;
    }
}
