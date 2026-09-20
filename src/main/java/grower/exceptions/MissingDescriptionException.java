package grower.exceptions;

/**
 * Represents an error caused by a missing required description.
 */
public class MissingDescriptionException extends GrowerException {
    /**
     * Creates an exception with the specified message.
     *
     * @param message Description of the error.
     */
    public MissingDescriptionException(String message) {
        super(message);
    }
}
