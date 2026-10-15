package ace.exception;

/**
 * Thrown when user input isn't a command Ace recognizes at all, such as an
 * unrecognized command word, or a recognized command with malformed arguments
 * (e.g. a non-numeric argument to "mark", or a "deadline"/"event" missing its
 * required "/by", "/from", or "/to" clause).
 */
public class WrongCommandException extends AceException {

    /**
     * Creates a new exception for input that couldn't be understood as a command.
     *
     * @param message a specific, user-facing explanation of what was wrong
     *     with the input and how to fix it (e.g. what usage the command
     *     expects), or — for a genuinely unrecognized command word — the
     *     full input itself.
     */
    public WrongCommandException(String message) {
        super(message);
    }
}
