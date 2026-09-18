package oreo.model;


/** Represents whether a task is complete. */
public enum TaskStatus {
    /** A task that has not been completed yet. */
    TODO(" "),

    /** A completed task. */
    DONE("X");

    private final String icon;

    TaskStatus(String icon) {
        this.icon = icon;
    }

    /** Returns the character used in the task's display text. */
    public String getIcon() {
        return icon;
    }
}
