package rudra.exception;

/**
 * Represents an error caused by invalid user input to Rudra.
 */
public class RudraException extends Exception {
    /**
     * Creates a Rudra-specific exception with a user-facing message.
     *
     * @param message Explanation of what went wrong.
     */
    public RudraException(String message) {
        super(message);
    }
}
