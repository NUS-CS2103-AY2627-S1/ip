package jarvis.exceptions;

/**
 * Represents a task number that is outside the valid task-list range.
 */
public class InvalidTaskNumberException extends JarvisException {

    /**
     * Creates an exception with the specified message.
     *
     * @param message Error message to display.
     */
    public InvalidTaskNumberException(String message) {
        super(message);
    }
}
