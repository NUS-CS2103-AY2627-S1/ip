package sal;

/**
 * Exception thrown when Sal cannot process a user command as expected.
 */
public class SalException extends Exception {
    /**
     * Creates an exception with a message to show the user.
     *
     * @param message Explanation or correct-format hint for the user.
     */
    public SalException(String message) {
        super(message);
    }
}
