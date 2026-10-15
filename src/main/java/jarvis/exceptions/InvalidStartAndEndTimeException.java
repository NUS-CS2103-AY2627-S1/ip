package jarvis.exceptions;

/**
 * Represents an invalid pair of start and end date and time values.
 */
public class InvalidStartAndEndTimeException extends JarvisException {

    /**
     * Creates an exception with the specified message.
     *
     * @param message Error message to display.
     */
    public InvalidStartAndEndTimeException(String message) {
        super(message);
    }
}
