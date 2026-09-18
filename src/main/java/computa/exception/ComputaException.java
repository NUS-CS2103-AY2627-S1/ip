package computa.exception;

/**
 * Represents an input error specific to Computa.
 */
public class ComputaException extends Exception {
    /**
     * Creates an input error with a user-facing message.
     *
     * @param message explanation of the invalid input.
     */
    public ComputaException(String message) {
        super(message);
    }
}
