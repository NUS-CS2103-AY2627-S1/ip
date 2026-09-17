package notmarth.model;

/**
 * Identifies the kind of task and its display marker.
 */
public enum TaskType {
    /** A plain {@link Task} without a specialized task type. */
    GENERIC(""),

    /** A task without a date or time. */
    TODO("[T]"),

    /** A task with a due date or time. */
    DEADLINE("[D]"),

    /** A task with a start and end date or time. */
    EVENT("[E]");

    private final String displayMarker;

    /**
     * Creates a task type with the marker used in console output.
     *
     * @param displayMarker the marker displayed before the completion status.
     */
    TaskType(String displayMarker) {
        this.displayMarker = displayMarker;
    }

    /**
     * Returns the marker used when displaying this task type.
     *
     * @return the task type marker, or an empty string for a generic task.
     */
    public String getDisplayMarker() {
        return displayMarker;
    }
}
