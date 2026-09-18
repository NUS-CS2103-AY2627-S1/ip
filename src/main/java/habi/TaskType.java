package habi;

/**
 * Identifies each supported task kind and its display icon.
 */
public enum TaskType {
    /** A task without timing details. */
    TODO("T"),
    /** A task with a due date. */
    DEADLINE("D"),
    /** A task with start and end values. */
    EVENT("E");

    private final String icon;

    /**
     * Associates a task kind with its one-letter display icon.
     *
     * @param icon Icon shown in task listings.
     */
    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns this task kind's display icon.
     *
     * @return One-letter task icon.
     */
    public String getIcon() {
        return icon;
    }
}
