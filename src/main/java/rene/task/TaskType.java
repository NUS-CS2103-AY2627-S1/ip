package rene.task;

/**
 * Represents the supported task categories and their display icons.
 */
public enum TaskType {
    /** Identifies a task without an attached date or time. */
    TODO("T"),
    /** Identifies a task with a due date. */
    DEADLINE("D"),
    /** Identifies a task with a start and end value. */
    EVENT("E");

    private final String icon;

    /**
     * Creates a task type with its display icon.
     *
     * @param icon the letter used to display this task type.
     */
    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the icon used to display this task type.
     *
     * @return the task type icon.
     */
    public String getIcon() {
        return icon;
    }
}
