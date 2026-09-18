package plana.task;

/**
 * Represents the completion state of a task.
 */
public enum CompletionStatus {
    /** A task that has not been completed. */
    NOT_DONE(" "),

    /** A task that has been completed. */
    DONE("X");

    private final String statusIcon;

    /**
     * Creates a completion status with its display icon.
     *
     * @param statusIcon the icon used when displaying this status.
     */
    CompletionStatus(String statusIcon) {
        this.statusIcon = statusIcon;
    }

    /**
     * Returns the icon used when displaying this status.
     *
     * @return the status icon
     */
    public String getStatusIcon() {
        return statusIcon;
    }
}
