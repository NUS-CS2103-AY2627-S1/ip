package fifi.exception;

/**
 * Represents an error caused by an unrecognized command.
 */
public class InvalidCommandException extends FifiException {
    /**
     * Creates an exception with the message to show to the user.
     *
     * @param message the error message
     */
    public InvalidCommandException(String message) {
        super(message);
    }
}
