package labubu;

/**
 * Signals a command that Labubu does not support.
 */
public class UnrecognisedCommandException extends Exception {
    /**
     * Creates an exception with the message shown for unknown commands.
     */
    public UnrecognisedCommandException() {
        super("Hmm, I don't recognise that command.");
    }
}
