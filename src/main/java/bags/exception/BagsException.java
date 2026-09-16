package bags.exception;

/**
 * Represents a custom exception used to handle errors specific to the
 * Bags application.
 */
public class BagsException extends Exception {

    /**
     * Creates a BagsException with the specified error message.
     *
     * @param message the message describing the error
     */
    public BagsException(String message) {
        super(message);
    }
}
