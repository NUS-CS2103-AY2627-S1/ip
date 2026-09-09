package trayce.task;

/** Identifies the supported kinds of tasks. */
public enum TaskType {
    /** Represents a todo task. */
    TODO("T"),
    /** Represents a deadline task. */
    DEADLINE("D"),
    /** Represents an event task. */
    EVENT("E");

    private final String icon;

    /**
     * Creates a task type with the given icon representation.
     *
     * @param icon the string icon representing this task type
     */
    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the short icon used when displaying this task type.
     *
     * @return the short icon string representing this task type
     */
    public String getIcon() {
        return icon;
    }
}
