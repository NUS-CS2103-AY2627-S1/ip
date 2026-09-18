package altair.task;

/**
 * The fixed categories of tasks supported by Altair.
 */
public enum TaskType {
    /** A basic task without scheduling details. */
    TODO("T"),

    /** A task with a completion date. */
    DEADLINE("D"),

    /** A task with start and end dates. */
    EVENT("E");

    private final String icon;

    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the one-letter marker used when displaying this task type.
     *
     * @return the task type marker.
     */
    public String getIcon() {
        return icon;
    }
}
