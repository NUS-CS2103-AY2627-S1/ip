package mochi;

/**
 * Represents an error caused by an invalid command entered by the user.
 */
public class MochiException extends Exception {
    /**
     * Creates a MochiException with the given error message.
     *
     * @param message the error message
     */
    public MochiException(String message) {
        super(message);
    }
}
