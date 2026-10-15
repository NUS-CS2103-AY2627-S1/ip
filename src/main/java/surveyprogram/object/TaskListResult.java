package surveyprogram.object;

/**
 * Describes the outcome of a task-list operation together with its user-facing response.
 *
 * @param response user-facing description of the outcome
 * @param status semantic status of the operation
 */
public record TaskListResult(String response, Status status) {
    /** Identifies whether a task-list operation succeeded, had no results, or failed. */
    public enum Status {
        SUCCESS,
        EMPTY,
        ERROR
    }
}
