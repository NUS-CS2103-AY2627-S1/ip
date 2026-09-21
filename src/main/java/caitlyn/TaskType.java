package caitlyn;

/**
 * The kinds of tasks that Caitlyn can store.
 *
 * <p>Each kind also owns the one-letter marker used when the task is displayed.
 * Keeping the marker with the enum avoids scattering task-type strings across
 * the task classes.</p>
 */
public enum TaskType {
    /** A task without a date or time attached. */
    TODO("T"),

    /** A task that must be completed by a specified date or time. */
    DEADLINE("D"),

    /** A task with a start and end date or time. */
    EVENT("E"),

    /** An action completed once within an inclusive date/time window. */
    WITHIN("W");

    /** The marker shown before a task description. */
    private final String marker;

    /**
     * Creates a task type with its display marker.
     *
     * @param marker the one-letter marker used in task output.
     */
    TaskType(String marker) {
        this.marker = marker;
    }

    /**
     * Returns the marker used when displaying this task type.
     *
     * @return the task type marker.
     */
    public String getMarker() {
        return marker;
    }

    /**
     * Returns the task type represented by a storage marker.
     *
     * @param marker the marker read from the task file.
     * @return the matching task type.
     * @throws IllegalArgumentException if the marker is not recognized.
     */
    public static TaskType parseMarker(String marker) {
        for (TaskType taskType : values()) {
            if (taskType.marker.equals(marker)) {
                return taskType;
            }
        }
        throw new IllegalArgumentException("Unknown saved task type: " + marker);
    }
}
