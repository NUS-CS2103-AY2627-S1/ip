package megia.exception;

/**
 * Reports that a task operation cannot resolve a user-facing task ID.
 */
public class TaskNotFoundException extends MegiaException {
    /**
     * Creates an error that accounts for both an empty list and an invalid ID.
     *
     * @param taskId Requested one-based task ID.
     * @param taskCount Number of tasks currently stored.
     * @param operation Operation the user attempted, such as {@code mark}.
     */
    public TaskNotFoundException(int taskId, int taskCount, String operation) {
        super(taskCount == 0 ? ErrorCode.TASK_LIST_EMPTY : ErrorCode.TASK_NOT_FOUND,
                taskCount == 0
                        ? new Object[] {operation}
                        : new Object[] {taskId, taskCount});
    }
}
