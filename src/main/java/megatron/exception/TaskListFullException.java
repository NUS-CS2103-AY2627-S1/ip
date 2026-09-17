package megatron.exception;

/**
 * Reports that no more tasks can be stored.
 */
public final class TaskListFullException extends MegatronException {
    /**
     * Creates an error for a task list that is full.
     */
    public TaskListFullException() {
        super("The task list is full. Remove a task before adding another.");
    }
}
