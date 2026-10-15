package gongrilla.task;

/**
 * Represents the supported types of tasks that an add command can create.
 */
public enum TaskType {
    /** Identifies a task without a scheduled date. */
    TODO,
    /** Identifies a task with a due date. */
    DEADLINE,
    /** Identifies a task with a start and end time. */
    EVENT
}
