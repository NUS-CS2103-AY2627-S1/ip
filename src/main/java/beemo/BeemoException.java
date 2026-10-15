package beemo;

/**
 * Represents an error caused by a command that Beemo cannot process.
 */
public class BeemoException extends Exception {
    /**
     * Creates an exception containing a user-facing explanation of the error.
     *
     * @param message Explanation of the error.
     */
    public BeemoException(String message) {
        super(message);
    }
}
