package groot;

/**
 * Provides an immutable view of a task without exposing its mutable completion state.
 *
 * @param number One-based position in the main task list.
 * @param type Human-readable task type.
 * @param description Task description.
 * @param isDone Whether the task is completed.
 * @param schedule Display text for the deadline or event, or empty for a todo.
 */
public record TaskSummary(int number, String type, String description, boolean isDone, String schedule) {
}
