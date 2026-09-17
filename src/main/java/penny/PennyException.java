package penny;

/**
 * Custom exception used in the penny package.
 */
public class PennyException extends Exception {
    /**
     * Returns PennyException.
     *
     * @param message string to describe the exception.
     */
    public PennyException(String message) {
        super(message);
    }

    /**
     * Returns PennyException.
     *
     * @param message string to describe the exception.
     * @param cause throwable cause of the exception.
     */
    public PennyException(String message, Throwable cause) {
        super(message, cause);
    }
}
