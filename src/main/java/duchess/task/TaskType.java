package duchess.task;

/**
 * The supported categories of tasks in Duchess.
 */
public enum TaskType {
    /** A basic todo task. */
    TODO("T"),

    /** A task with a deadline. */
    DEADLINE("D"),

    /** A task associated with an event time. */
    EVENT("E");

    /** The one-letter icon used when displaying this task type. */
    private final String icon;

    /**
     * Creates a task type with its display icon.
     *
     * @param icon the display icon
     */
    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the display icon for this task type.
     *
     * @return the task type icon
     */
    public String getIcon() {
        return icon;
    }
}
