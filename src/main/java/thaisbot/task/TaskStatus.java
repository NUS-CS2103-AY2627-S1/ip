package thaisbot.task;

/**
 * Enumeration representing the completion status of a task.
 */
public enum TaskStatus {
    DONE("X"),
    NOT_DONE(" ");

    private final String icon;

    TaskStatus(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the single-character icon used to display status.
     * @return icon string
     */
    public String getIcon() {
        return icon;
    }
}
