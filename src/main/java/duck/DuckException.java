package duck;

/**
 * Represents an error caused by invalid user input in Duck.
 */
public class DuckException extends Exception {
    /** Serialization identifier required by the Exception contract. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a Duck-specific exception with the given error message.
     *
     * @param message Explanation of the error.
     */
    public DuckException(String message) {
        super(message);
    }
}
