package miku.task;

/** Defines the finite set of task kinds and their persisted marker values. */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String marker;

    TaskType(String marker) {
        this.marker = marker;
    }

    /** Returns the marker used for this task type in task displays and saved data. */
    public String getMarker() {
        return marker;
    }

    /** Returns the task type represented by a persisted marker. */
    public static TaskType fromMarker(String marker) {
        return switch (marker) {
            case "T" -> TODO;
            case "D" -> DEADLINE;
            case "E" -> EVENT;
            default -> throw new IllegalArgumentException("Unknown task type marker: " + marker);
        };
    }
}
