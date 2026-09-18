package geek.exception;

/**
 * Represents an error caused by an invalid Geek command or task operation.
 */
public class GeekException extends RuntimeException {
    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message Explanation of the invalid operation.
     */
    public GeekException(String message) {
        super(message);
    }
}
