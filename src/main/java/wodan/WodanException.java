package wodan;

/**
 * A chatbot-specific error, such as an empty description or an unknown command.
 */
public class WodanException extends Exception {
    /**
     * Creates an exception with the given user-facing message.
     *
     * @param message Text to show the user.
     */
    public WodanException(String message) {
        super(message);
    }
}
