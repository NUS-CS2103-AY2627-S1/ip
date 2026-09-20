package fifi.exception;

/**
 * Represents an error caused by a missing or invalid command description.
 */
public class InvalidDescriptionException extends FifiException {
    /**
     * Creates an exception with the message to show to the user.
     *
     * @param message the error message
     */
    public InvalidDescriptionException(String message) {
        super(message);
    }
}
