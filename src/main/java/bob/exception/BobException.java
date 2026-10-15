package bob.exception;

/**
 * Represents a custom exception specific to the Bob application.
 */
public class BobException extends Exception {

    /**
     * Constructs a BobException with the specified error message.
     *
     * @param message the detail error message
     */
    public BobException(String message) {
        super(message);
    }
}
