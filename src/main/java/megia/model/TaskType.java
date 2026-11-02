package megia.model;

/**
 * Identifies the concrete type of a task in persistent storage.
 */
public enum TaskType {
    /** A task that must be completed by a specified date or time. */
    DEADLINE,
    /** A task that occurs between specified start and end times. */
    EVENT,
    /** A task without a deadline or scheduled time range. */
    TODO
}
