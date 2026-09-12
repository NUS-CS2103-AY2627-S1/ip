package bob.exception;

/** Represents an error caused by invalid input to Bob. */
public class BobException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message explanation of the invalid input or operation.
     */
    public BobException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a user-facing explanation and underlying cause.
     *
     * @param message explanation of the invalid input or operation.
     * @param cause exception that caused this failure.
     */
    public BobException(String message, Throwable cause) {
        super(message, cause);
    }
}
