package happybot;

/**
 * Represents an error caused by an invalid HappyBot command.
 */
public class HappyBotException extends Exception {

    /**
     * Creates an exception with a message that explains the invalid command.
     *
     * @param message The explanation of the input error.
     */
    public HappyBotException(String message) {
        super(message);
    }
}
