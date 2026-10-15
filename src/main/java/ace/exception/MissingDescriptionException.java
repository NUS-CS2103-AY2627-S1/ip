package ace.exception;

/**
 * Thrown when a command that requires a description (e.g. "todo", "deadline",
 * "event", "delete", "date", "find") is given without one, such as typing
 * "todo" or "deadline " with nothing after it.
 */
public class MissingDescriptionException extends AceException {

    /**
     * Creates a new exception for a command that was missing a required
     * piece of information.
     *
     * @param message a specific, user-facing explanation of what's missing
     *     and how to fix it (e.g. what usage the command expects).
     */
    public MissingDescriptionException(String message) {
        super(message);
    }
}
