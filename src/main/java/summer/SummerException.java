package summer;

/**
 * Represents an error caused by invalid input given to Summer.
 */
public class SummerException extends Exception {
    /**
     * Creates an exception with a message that can be shown to the user.
     *
     * @param message explanation of what went wrong
     */
    public SummerException(String message) {
        super(message);
    }
}
