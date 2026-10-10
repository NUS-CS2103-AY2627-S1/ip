package zsiggy;

/**
 * Represents an error caused by invalid user input or commands.
 */
public class ZsiggyException extends Exception {

    /**
     * Creates a ZsiggyException with the specified error message.
     *
     * @param message the error message
     */
    public ZsiggyException(String message) {
        super(message);
    }
}
