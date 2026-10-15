package peter.exception;

/**
 * Represents an error caused by invalid task data or user input.
 */
public class PeterException extends Exception {

    /**
     * Constructs an exception with a message suitable for displaying to the user.
     *
     * @param message Description of the error.
     */
    public PeterException(String message) {
        super(message);
    }
}
