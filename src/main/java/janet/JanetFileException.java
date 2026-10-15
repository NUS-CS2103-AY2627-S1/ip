package janet;

/**
 * Represents an exception that occurs during Janet task management operations.
 */
public class JanetFileException extends Exception {
    /**
     * Creates a JanetException with the specified error message.
     *
     * @param message Description of the error.
     */
    public JanetFileException(String message) {
        super(message);
    }

    /**
     * Creates a JanetException with the specified error message and cause.
     *
     * @param message Description of the error.
     * @param err Cause of the exception.
     */
    public JanetFileException(String message, Throwable err) {
        super(message, err);
    }
}
