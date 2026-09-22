package mono.exception;

/**
 * Indicates that a requested task ID does not refer to an existing task.
 */
public class NonExistentException extends MonoException {
    /**
     * Creates an exception for a task ID that is not in the task list.
     *
     * @param message user-facing explanation of the error
     */
    public NonExistentException(String message) {
        super(message);
    }
}
