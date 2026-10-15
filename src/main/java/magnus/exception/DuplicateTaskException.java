package magnus.exception;

/**
 * Indicates that a command would create a task with the same details as an existing task.
 */
public class DuplicateTaskException extends MagnusException {
    /**
     * Creates an exception describing the duplicate task.
     *
     * @param message The detail message describing the error.
     */
    public DuplicateTaskException(String message) {
        super(message);
    }
}
