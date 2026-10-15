package chocolate.exception;

/**
 * Represents an error caused by an invalid Chocolate command or task reference.
 */
public class ChocolateException extends Exception {
    /**
     * Creates an exception containing a user-facing explanation.
     *
     * @param message Explanation of the error.
     */
    public ChocolateException(String message) {
        super(message);
    }
}
