package turtley.model;

/**
 * Identifies the supported kinds of tasks.
 */
public enum TaskType {
    /** A task without a deadline or time range. */
    TODO,
    /** A task with a deadline. */
    DEADLINE,
    /** A task with a start and end time. */
    EVENT
}
