package ayre.exceptions;

/**
 * Main wrapper for exceptions thrown within Ayre.
 */
public class AyreException extends Exception {
    /**
     * Initializes the exception by passing the message on.
     *
     * @param message Error message.
     */
    public AyreException(String message) {
        super(message);
    }
}
