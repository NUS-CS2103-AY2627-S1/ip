package minty.exception;

/**
 * Represents an error caused by an invalid command entered in Minty.
 */
public class MintyException extends Exception {
    /**
     * Creates a Minty-specific exception with a message suitable for the user.
     *
     * @param message explanation of the invalid input.
     */
    public MintyException(String message) {
        super(message);
    }
}
