package kia.task;

/**
 * Supported types of tasks.
 */
public enum TaskType {
    /** A task without an associated date or time. */
    TODO("T"),
    /** A task that must be completed by a date or time. */
    DEADLINE("D"),
    /** A task with a start and end date or time. */
    EVENT("E");

    private final String icon;

    /**
     * Creates a task type with its display icon.
     *
     * @param icon the icon used when displaying this type
     */
    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the display icon for this task type.
     *
     * @return the type icon
     */
    public String getIcon() {
        return icon;
    }
}
