package tony.task;

/**
 * Represents the supported kinds of tasks and their list-display icons.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    /** The icon displayed before a task's completion status. */
    private final String icon;

    /**
     * Creates a task type with its list-display icon.
     *
     * @param icon the letter shown for this type of task.
     */
    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the icon used when displaying this task type.
     *
     * @return the task type icon.
     */
    public String getIcon() {
        return icon;
    }
}
