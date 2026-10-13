package ruby;

/**
 * Represents a user-facing error that Ruby can recover from.
 */
public class RubyException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a Ruby-specific exception with a user-facing explanation.
     *
     * @param message Explanation of the error.
     */
    public RubyException(String message) {
        super(message);
    }
}
