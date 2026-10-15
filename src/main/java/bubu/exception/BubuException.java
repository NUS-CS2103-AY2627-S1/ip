package bubu.exception;

/**
 * Represents an error that can be shown to the chatbot user.
 */
public class BubuException extends Exception {

    /**
     * Creates an exception with a user-facing message.
     *
     * @param message error message to display.
     */
    public BubuException(String message) {
        super(message);
    }

}
