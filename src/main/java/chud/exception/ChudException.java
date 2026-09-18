package chud.exception;

/**
 * Signals that the user typed something the chatbot could not act on
 * (an unknown command, or a known command used incorrectly).
 */
public class ChudException extends Exception {
    /** Creates a ChudException with the given user-facing, correctable explanation. */
    public ChudException(String message) {
        super(message);
    }
}
