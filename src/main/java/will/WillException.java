package will;

/**
 * Represents an error specific to the Will chatbot, e.g. an invalid or
 * incomplete command entered by the user.
 */
public class WillException extends Exception {
    /**
     * Creates a WillException with the given user-facing message.
     *
     * @param message A user-facing description of what went wrong.
     */
    public WillException(String message) {
        super(message);
    }
}
