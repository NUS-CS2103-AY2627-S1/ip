package yuno.exception;

/**
 * Represents a task number that is malformed or outside the task list.
 */
public class InvalidTaskNumberException extends YunoException {
    /**
     * Creates an exception with the specified error message.
     *
     * @param message Error message to display.
     */
    public InvalidTaskNumberException(String message) {
        super(message);
    }
}
