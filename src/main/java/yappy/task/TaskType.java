package yappy.task;

/**
 * Represents the fixed task categories Yappy can display.
 */
public enum TaskType {
    /** A task without date or time information. */
    TODO("T"),

    /** A task that should be completed by a specific date or time. */
    DEADLINE("D"),

    /** A task that happens across a time range. */
    EVENT("E");

    /** Short icon shown in the task list. */
    private final String icon;

    /**
     * Creates a task type with its list display icon.
     */
    TaskType(String icon) {
        this.icon = icon;
    }

    /** Returns the short icon shown in the task list. */
    public String getIcon() {
        return icon;
    }
}
