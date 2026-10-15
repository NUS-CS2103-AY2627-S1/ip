package lenzabot;

/**
 * Represents user-facing errors specific to LenZaBot commands.
 */
public class LenZaBotException extends Exception {
    /**
     * Creates an exception with the given user-facing error message.
     *
     * @param message Error message to show to the user.
     */
    public LenZaBotException(String message) {
        super(message);
    }
}
