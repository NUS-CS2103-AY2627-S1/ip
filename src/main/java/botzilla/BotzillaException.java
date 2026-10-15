package botzilla;

/**
 * Represents an error caused by invalid user input or a recoverable
 * failure during command processing (e.g. malformed dates, missing
 * task descriptions, or an unrecognized command).
 */
public class BotzillaException extends Exception {

    /**
     * Constructs a BotzillaException with the given user-facing message.
     *
     * @param message description of what went wrong, shown to the user
     */
    public BotzillaException(String message) {
        super(message);
    }
}
