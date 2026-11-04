package soar.exception;

/**
 * Signals that a mark, unmark, or delete command does not identify an existing task.
 */
public class InvalidTaskNumberException extends SoarException {
    /**
     * Creates an exception with guidance for selecting a valid task.
     *
     * @param message Explanation of the task-number problem.
     */
    public InvalidTaskNumberException(String message) {
        super(message);
    }
}
