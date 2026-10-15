package janet;

/**
 * Represents an exception that occurs during Janet task management operations.
 */
public class JanetException extends Exception {
    /**
     * Creates a JanetException with the specified error message.
     *
     * @param message Description of the error.
     */
    public JanetException(String message) {
        super(message);
    }

    /**
     * Creates a JanetException with the specified error message and cause.
     *
     * @param message Description of the error.
     * @param err Cause of the exception.
     */
    public JanetException(String message, Throwable err) {
        super(message, err);
    }
}
