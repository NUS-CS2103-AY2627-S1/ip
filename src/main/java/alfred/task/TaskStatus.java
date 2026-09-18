package alfred.task;

/**
 * Represents the completion status of a task.
 */
public enum TaskStatus {
    PENDING(" "),
    DONE("X");

    private final String icon;

    TaskStatus(String icon) {
        this.icon = icon;
    }

    /** Returns the icon used when displaying this task status. */
    public String getIcon() {
        return icon;
    }
}
