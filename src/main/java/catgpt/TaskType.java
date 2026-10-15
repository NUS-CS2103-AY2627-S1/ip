package catgpt;

/**
 * Identifies a task category and its marker in CatGPT's text output.
 */
public enum TaskType {
    /** A task without associated timing details. */
    TODO("T"),

    /** A task that must be completed by a date. */
    DEADLINE("D"),

    /** A task that occurs over a specified period. */
    EVENT("E");

    private final String marker;

    /**
     * Creates a task type with the marker used when displaying a task.
     *
     * @param marker The single-letter marker for this task type.
     */
    TaskType(String marker) {
        this.marker = marker;
    }

    /**
     * Returns the marker used when displaying this task type.
     *
     * @return The task type marker.
     */
    public String getMarker() {
        return marker;
    }
}
