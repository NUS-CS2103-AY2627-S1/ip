package noms.exception;

/**
 * Indicates that a mark, unmark, or delete command contains an invalid task number.
 */
public class InvalidTaskNumberException extends NomsException {
    /**
     * Creates the exception with a message explaining the task-number problem.
     *
     * @param message the explanation shown to the user
     */
    public InvalidTaskNumberException(String message) {
        super(message);
    }
}
