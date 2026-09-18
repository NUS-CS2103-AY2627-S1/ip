package chris.exception;

/**
 * Represents an error that the chatbot can explain to the user.
 */
public class ChrisException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a user-facing explanation of the error.
     *
     * @param message Explanation of the invalid command.
     */
    public ChrisException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a user-facing explanation and its underlying cause.
     *
     * @param message Explanation of the error.
     * @param cause Underlying cause of the error.
     */
    public ChrisException(String message, Throwable cause) {
        super(message, cause);
    }
}
