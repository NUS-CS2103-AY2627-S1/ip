package elsa;

/**
 * Signals that the user's input could not be carried out, and carries the
 * explanation to show them. Extends Exception rather than RuntimeException so
 * the compiler insists that callers deal with it.
 */
public class ElsaException extends Exception {
    /**
     * Creates an exception carrying an explanation for the user.
     *
     * @param message what went wrong, written for the user rather than the programmer
     */
    public ElsaException(String message) {
        super(message);
    }
}
