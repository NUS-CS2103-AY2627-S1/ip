package daddy.task;

/** Identifies the kind of task and its display marker. */
public enum TaskType {
    /** Identifies a task without a date or time. */
    TODO("[T]"),
    /** Identifies a task due by a date or time. */
    DEADLINE("[D]"),
    /** Identifies a task with start and end times. */
    EVENT("[E]"),
    /** Identifies a generic task without a specialized type icon. */
    GENERAL("");

    /** Stores the marker shown before tasks of this type. */
    private final String icon;

    /**
     * Creates a task type with its display icon.
     *
     * @param icon the marker shown before this type of task
     */
    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the marker used when displaying this task type.
     *
     * @return the task type icon
     */
    public String getIcon() {
        return icon;
    }
}
