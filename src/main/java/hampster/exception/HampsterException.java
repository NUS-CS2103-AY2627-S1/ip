package hampster.exception;

/**
 * Represents an application-specific checked exception for Hampster.
 */
public class HampsterException extends Exception {

    /**
     * Creates a Hampster exception with the specified message.
     *
     * @param message a description of the error
     */
    public HampsterException(String message) {
        super(message);
    }
}
