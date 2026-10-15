package clarry;

/**
 * Represents an error caused by an invalid Clarry command or command format.
 */
public class ClarryException extends Exception {
    /**
     * Creates an exception with a user-friendly error message.
     *
     * @param message explanation of the invalid command
     */
    public ClarryException(String message) {
        super(message);
    }
}
