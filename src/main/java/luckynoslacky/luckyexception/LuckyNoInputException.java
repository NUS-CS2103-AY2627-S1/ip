package luckynoslacky.luckyexception;

/**
 * Indicates that a user's command cannot be parsed or does not satisfy the
 * chatbot's input requirements.
 */
public class LuckyNoInputException extends Exception {
    /**
     * Creates an input error with a user-facing message.
     *
     * @param message explanation of the invalid input
     */
    public LuckyNoInputException(String message) {
        super(message);
    }
}
