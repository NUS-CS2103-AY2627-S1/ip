package toothless.exception;

/**
 * Represents an expected error caused by invalid user input.
 */
public class ToothlessException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an input error with a friendly message for the user.
     *
     * @param message message explaining how to correct the input.
     */
    public ToothlessException(String message) {
        super(message);
    }
}
