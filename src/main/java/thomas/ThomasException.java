package thomas;

/**
 * Represents an exception raised by the Thomas chatbot.
 */
public class ThomasException extends Exception {
    /**
     * Creates a Thomas exception with the given message.
     *
     * @param message Description of the error.
     */
    public ThomasException(String message) {
        super(message);
    }
}
