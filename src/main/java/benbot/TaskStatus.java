package benbot;

/** The possible completion states of a task. */
public enum TaskStatus {
    /** A task that has not been completed. */
    NOT_DONE("[ ]"),

    /** A task that has been completed. */
    DONE("[X]");

    private final String icon;

    TaskStatus(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the status icon used in the task list.
     *
     * @return the icon that represents this status.
     */
    public String getIcon() {
        return icon;
    }
}
