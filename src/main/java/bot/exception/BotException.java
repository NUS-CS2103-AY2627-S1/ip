package bot.exception;

/**
 * Signals a problem with a user command that Bot can explain in plain
 * language, e.g. a missing description or an unrecognized command word.
 */
public class BotException extends Exception {

    /**
     * Creates a new exception carrying a plain-language message for the user.
     *
     * @param message The message to show the user, e.g. an "OOPS!!!" explanation.
     */
    public BotException(String message) {
        super(message);
    }
}
