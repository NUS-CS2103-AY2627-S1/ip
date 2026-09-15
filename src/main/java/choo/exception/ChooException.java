package choo.exception;

/**
 * Represents an error that CHOO can explain to the user.
 */
public class ChooException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a CHOO-specific exception with a user-facing explanation.
     *
     * @param message explanation of the invalid input.
     */
    public ChooException(String message) {
        super(message);
    }
}
