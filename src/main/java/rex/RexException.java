package rex;

/**
 * Signals that the user's input could not be understood or acted on
 * (e.g. an unrecognized command, a missing description, or an invalid
 * task number). The message is shown to the user as-is, so it should be
 * a complete, user-facing sentence.
 */
public class RexException extends Exception {
    public RexException(String message) {
        super(message);
    }
}
