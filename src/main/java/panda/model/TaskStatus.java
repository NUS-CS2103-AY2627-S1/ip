package panda.model;

/**
 * Completion states available for a task.
 */
public enum TaskStatus {
    NOT_DONE(" "),
    DONE("X");

    private final String icon;

    TaskStatus(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the short display icon for this status.
     *
     * @return the status icon
     */
    public String getIcon() {
        return icon;
    }
}
