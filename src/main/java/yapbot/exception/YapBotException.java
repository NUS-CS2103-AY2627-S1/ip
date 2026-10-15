package yapbot.exception;

/**
 * Signals a user-facing error in yapBot, e.g. an unrecognized command or
 * invalid input, as opposed to an unexpected internal failure.
 */
public class YapBotException extends Exception {

    /**
     * Creates an exception with the given user-facing message.
     *
     * @param msg the message to show the user.
     */
    public YapBotException(String msg) {
        super(msg);
    }
}
