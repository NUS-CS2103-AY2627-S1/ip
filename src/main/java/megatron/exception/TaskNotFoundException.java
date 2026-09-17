package megatron.exception;

/**
 * Reports that a requested task number is outside the current list.
 */
public final class TaskNotFoundException extends MegatronException {
    /**
     * Creates an error for a task number that is not in the list.
     */
    public TaskNotFoundException() {
        super("That task number does not exist.");
    }
}
