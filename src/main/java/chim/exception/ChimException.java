package chim.exception;

/**
 * Represents an exception specific to Chim, thrown when user input cannot
 * be understood or does not meet Chim's expected command format.
 */
public class ChimException extends Exception {

    /**
     * Creates a ChimException with the given message.
     *
     * @param message Explanation of what went wrong, shown to the user.
     */
    public ChimException(String message) {
        super(message);
    }
}
