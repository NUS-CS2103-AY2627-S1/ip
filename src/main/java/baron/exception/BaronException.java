// javadocs created by AI

package baron.exception;

/**
 * Base exception for all user-facing errors handled by the Baron application.
 */
public class BaronException extends Exception {
    /**
     * Constructs a Baron-specific exception with a descriptive message.
     *
     * @param message the error message to display to the user.
     */
    public BaronException(String message) {
        super(message);
    }
}
