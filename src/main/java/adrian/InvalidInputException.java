package adrian;

/**
 * Represents an error caused by an invalid command or input from the user.
 */
public class InvalidInputException extends Exception {

    /**
     * Creates an exception with a message that explains the error to the user.
     *
     * @param message explanation of the error.
     */
    public InvalidInputException(String message) {
        super(message);
    }
}
