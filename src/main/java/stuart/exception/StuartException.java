package stuart.exception;

/**
 * Represents an error specific to the Stuart chatbot, e.g. an invalid or
 * incomplete command entered by the user.
 */
public class StuartException extends Exception {

    /**
     * Creates a new exception carrying the given user-facing error message.
     *
     * @param message the message to show the user
     */
    public StuartException(String message) {
        super(message);
    }
}
