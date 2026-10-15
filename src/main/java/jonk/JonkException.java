package jonk;

/**
 * Represents an error that Jonk can explain to the user and recover from.
 */
public class JonkException extends Exception {
    /**
     * Creates a Jonk error with the specified user-friendly message.
     *
     * @param message User-friendly explanation of the error.
     */
    public JonkException(String message) {
        super(message);
    }

    /**
     * Creates a Jonk error while preserving the lower-level cause for debugging.
     *
     * @param message User-friendly explanation of the error.
     * @param cause Lower-level exception that caused the error.
     */
    public JonkException(String message, Throwable cause) {
        super(message, cause);
    }
}
