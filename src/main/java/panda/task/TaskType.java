package panda.task;

/**
 * Identifies a task subtype using its display and storage marker.
 */
public enum TaskType {
    /** A date-free to-do task. */
    TODO("T"),

    /** A task with a deadline. */
    DEADLINE("D"),

    /** A task with a start and end date-time. */
    EVENT("E");

    private final String marker;

    /**
     * Creates a task type with its marker.
     *
     * @param marker the value used for display and storage.
     */
    TaskType(String marker) {
        this.marker = marker;
    }

    /**
     * Returns the task type's display and storage marker.
     *
     * @return the marker for this task type.
     */
    public String getMarker() {
        return marker;
    }

    /**
     * Returns the task type represented by a marker.
     *
     * @param marker the marker to interpret.
     * @return the matching task type.
     * @throws IllegalArgumentException if the marker is not recognized.
     */
    public static TaskType fromMarker(String marker) {
        for (TaskType taskType : values()) {
            if (taskType.marker.equals(marker)) {
                return taskType;
            }
        }
        throw new IllegalArgumentException("Unknown task type marker: " + marker);
    }
}
