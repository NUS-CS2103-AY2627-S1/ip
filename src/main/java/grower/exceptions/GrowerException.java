package grower.exceptions;

/**
 * Represents the base type for all application-specific exceptions.
 */
public class GrowerException extends Exception {
    /**
     * Creates an exception with the specified message.
     *
     * @param message Description of the error.
     */
    public GrowerException(String message) {
        super(message);
    }
}
