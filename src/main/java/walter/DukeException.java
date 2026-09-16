package walter;

/**
 * Represents an expected error caused by invalid user input.
 */
public class DukeException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a message describing the input error.
     *
     * @param message Explanation of the invalid input.
     */
    public DukeException(String message) {
        super(message);
    }

    /**
     * Creates an application exception while retaining the lower-level failure for diagnosis.
     *
     * @param message User-facing explanation of the failure.
     * @param cause Lower-level exception that caused the failure.
     */
    public DukeException(String message, Throwable cause) {
        super(message, cause);
    }
}
