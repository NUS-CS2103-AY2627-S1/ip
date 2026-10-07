package mary.exception;

/**
 * Represents an input or command error specific to MARY.
 */
public class MaryException extends Exception {
    /**
     * Creates an error with a message that can be shown to the user.
     *
     * @param message explanation of the problem and how to correct it.
     */
    public MaryException(String message) {
        super(message);
    }
}
