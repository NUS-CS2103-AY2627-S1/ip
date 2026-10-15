package magnus.exception;

/**
 * Indicates that a requested task number does not identify a stored task.
 */
public class TaskNotFoundException extends MagnusException {
    /**
     * Creates an exception describing the missing task.
     *
     * @param message The detail message describing the error.
     */
    public TaskNotFoundException(String message) {
        super(message);
    }
}
