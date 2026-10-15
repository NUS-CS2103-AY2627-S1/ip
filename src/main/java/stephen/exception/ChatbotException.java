package stephen.exception;

/**
 * Represents an error caused by an invalid chatbot command or user input.
 */
public class ChatbotException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a helpful message for the user.
     *
     * @param message explanation of the invalid input
     */
    public ChatbotException(String message) {
        super(message);
    }
}
