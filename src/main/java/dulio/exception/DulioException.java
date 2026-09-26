package dulio.exception;

/**
 * Represents an expected command error reported by Dulio.
 */
public class DulioException extends Exception {
    /**
     * Creates a command error with the specified message.
     *
     * @param message the user-facing error message.
     */
    public DulioException(String message) {
        super(message);
    }
}
