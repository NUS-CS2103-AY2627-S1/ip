package aether.task;

/**
 * Represents the kind of task stored by Aether.
 */
public enum TaskType {
    /** A task without a date or time. */
    TODO("T"),

    /** A task with a due date or time. */
    DEADLINE("D"),

    /** A task with a start and end time. */
    EVENT("E");

    private final String icon;

    /**
     * Creates a task type with its display icon.
     *
     * @param icon the single-letter icon shown in the task list
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
