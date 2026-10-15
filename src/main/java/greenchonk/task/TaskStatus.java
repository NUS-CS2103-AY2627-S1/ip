package greenchonk.task;

/**
 * Represents a task's completion status and the icon used to display it.
 */
public enum TaskStatus {
    /** The task has not been completed. */
    NOT_DONE(" "),

    /** The task has been completed. */
    DONE("X");

    private final String icon;

    /**
     * Creates a completion status with its display icon.
     *
     * @param icon the icon used to display the status.
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
