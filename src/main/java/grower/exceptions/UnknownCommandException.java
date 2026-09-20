package grower.exceptions;

/**
 * Represents an error caused by an unrecognized command.
 */
public class UnknownCommandException extends GrowerException {
    /**
     * Creates an exception with the specified message.
     *
     * @param message Description of the error.
     */
    public UnknownCommandException(String message) {
        super(message);
    }
}
