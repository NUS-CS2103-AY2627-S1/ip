package bot.exception;

/**
 * Represents an invalid chatbot command or invalid information within a command.
 */
public class BotException extends Exception {
    /**
     * Creates an exception with an explanation suitable for showing to the user.
     *
     * @param message explanation of the invalid input.
     */
    public BotException(String message) {
        super(message);
    }
}
