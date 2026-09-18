package seedu.mattchatbot;

/**
 * Signals that the user asked for something the bot cannot do, e.g. a command
 * it does not recognise, or a command missing a part it needs.
 * <p>
 * Introduced by the A-Exceptions extension. Its message is written for the
 * user rather than for a developer, because the bot prints it back directly.
 * It is a checked exception so that the compiler makes every command path
 * either handle the problem or pass it up to the one place that reports it.
 */
public class MattChatBotException extends Exception {

    /**
     * Creates an exception carrying an explanation the user will see.
     *
     * @param message what went wrong, and where possible how to fix it
     */
    public MattChatBotException(String message) {
        super(message);
    }
}
