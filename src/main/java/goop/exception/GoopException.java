package goop.exception;

/**
 * Represents an error caused by invalid user input.
 */
public class GoopException extends Exception {
    /** Serialization version for this exception type. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception containing guidance for correcting the input.
     *
     * @param message Explanation of the error and how to fix it.
     */
    public GoopException(String message) {
        super(message);
    }
}
