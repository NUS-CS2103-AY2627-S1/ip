package charlie.exception;

/**
 * Represents an error that Charlie can explain to the user without terminating unexpectedly.
 */
public class CharlieException extends RuntimeException {
    /**
     * Creates an exception containing a user-friendly explanation.
     *
     * @param message Explanation of the error.
     */
    public CharlieException(String message) {
        super(message);
    }
}
