package oreo.model;


/** Represents the category of a task. */
public enum TaskType {
    /** A task without a date or time. */
    TODO("T"),

    /** A task with a deadline. */
    DEADLINE("D"),

    /** A task with a start and end time. */
    EVENT("E");

    private final String marker;

    TaskType(String marker) {
        this.marker = marker;
    }

    /** Returns the marker used in the task's display text. */
    public String getMarker() {
        return marker;
    }
}
