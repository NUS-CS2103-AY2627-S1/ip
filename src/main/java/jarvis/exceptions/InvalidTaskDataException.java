package jarvis.exceptions;

/**
 * Represents an invalid task data in data file.
 */
public class InvalidTaskDataException extends JarvisException {

    /**
     * Creates an exception with the specified message.
     *
     * @param message Error message to display.
     */
    public InvalidTaskDataException(String message) {
        super(message);
    }
}
