package jonathan.task;

/** Represents the completion status of a task. */
public enum TaskStatus {
    NOT_DONE(" "),
    DONE("X");

    private final String icon;

    /**
     * Creates a status with the icon displayed in the task list.
     *
     * @param icon completion icon
     */
    TaskStatus(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the icon used to display this status.
     *
     * @return status icon
     */
    public String getIcon() {
        return icon;
    }
}
