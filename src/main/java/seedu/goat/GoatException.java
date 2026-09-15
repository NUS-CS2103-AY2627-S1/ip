package seedu.goat;

/**
 * Signals input that GOAT understands enough to reject politely.
 * <p>
 * Extends {@link Exception} rather than {@link RuntimeException} so the compiler forces
 * every command handler to declare it, which makes the error paths visible in the code.
 * The message carried here is written for the user and is printed verbatim.
 */
public class GoatException extends Exception {

    /**
     * Creates an exception whose message is shown directly to the user.
     *
     * @param message what went wrong, phrased as advice rather than as a stack trace.
     */
    public GoatException(String message) {
        super(message);
    }
}
