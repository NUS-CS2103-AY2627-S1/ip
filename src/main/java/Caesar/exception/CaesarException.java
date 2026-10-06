package caesar.exception;

/**
 * Exception used for user-facing validation and persistence errors.
 */
public class CaesarException extends Exception {
    private static final long serialVersionUID = 1L;

    /** Creates an exception with the supplied user-facing message. */
    public CaesarException(String message) {
        super(message);
    }
}
