package computah.exception;

/**
 * Represents an error caused by invalid Computah user input.
 */
public class ComputahException extends Exception {
    /**
     * Creates a Computah-specific exception with the given message.
     *
     * @param message explanation of the error.
     */
    public ComputahException(String message) {
        super(message);
    }
}
