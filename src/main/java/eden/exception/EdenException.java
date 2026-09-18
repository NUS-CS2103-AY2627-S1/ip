package eden.exception;

/**
 * Represents an application error that Eden can explain to the user.
 */
public class EdenException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an Eden error with a user-facing explanation.
     *
     * @param message explanation suitable for displaying to the user.
     */
    public EdenException(String message) {
        super(message);
    }

    /**
     * Creates an Eden error while preserving the lower-level cause.
     *
     * @param message explanation suitable for displaying to the user.
     * @param cause lower-level error that caused this exception.
     */
    public EdenException(String message, Throwable cause) {
        super(message, cause);
    }
}
