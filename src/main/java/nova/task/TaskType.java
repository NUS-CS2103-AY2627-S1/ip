package nova.task;

/**
 * Represents the supported kinds of tasks and their display prefixes.
 */
public enum TaskType {
    /** A basic task without a specialized type prefix. */
    BASIC(""),

    /** A task without a date or time constraint. */
    TODO("[T]"),

    /** A task with a deadline. */
    DEADLINE("[D]"),

    /** A task with a start and end time. */
    EVENT("[E]");

    private final String displayIcon;

    /**
     * Creates a task type with its display prefix.
     *
     * @param displayIcon the prefix displayed before a task
     */
    TaskType(String displayIcon) {
        this.displayIcon = displayIcon;
    }

    /**
     * Returns the prefix used when displaying this task type.
     *
     * @return the display prefix
     */
    public String getDisplayIcon() {
        return displayIcon;
    }
}
