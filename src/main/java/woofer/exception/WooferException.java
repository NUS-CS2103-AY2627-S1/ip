package woofer.exception;

/**
 * Represents an expected input error reported by Woofer.
 */
public class WooferException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a user-friendly error message.
     *
     * @param message explanation of the input error.
     */
    public WooferException(String message) {
        super(message);
    }
}
