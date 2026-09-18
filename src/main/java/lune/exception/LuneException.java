package lune.exception;

/**
 * Signals a problem with a user's command (e.g. missing/malformed details,
 * or a command Lune doesn't recognize). The message is written to be shown
 * to the user as-is.
 */
public class LuneException extends Exception {
    /**
     * Creates a new LuneException with a message meant to be shown to the
     * user as-is.
     */
    public LuneException(String message) {
        super(message);
    }
}
