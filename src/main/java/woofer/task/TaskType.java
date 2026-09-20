package woofer.task;

/**
 * The fixed set of task types supported by Woofer.
 */
public enum TaskType {
    /** Represents a task without an associated date. */
    TODO("T"),
    /** Represents a task with a completion date. */
    DEADLINE("D"),
    /** Represents a task with a start and end date. */
    EVENT("E");

    private final String icon;

    /**
     * Creates a task type with its display icon.
     *
     * @param icon one-letter icon shown in the task list.
     */
    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the display icon for this task type.
     *
     * @return the task type icon.
     */
    public String getIcon() {
        return icon;
    }
}
