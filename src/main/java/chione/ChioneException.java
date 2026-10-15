package chione;

/**
 * Signals that Chione cannot carry out what the user asked for.
 *
 * <p>The message carried by this exception is written for the user to read, so
 * the main loop can print it directly instead of translating an error code into
 * words. Anything the user could plausibly type wrongly is reported this way,
 * which keeps the checks next to the code that knows what "wrong" means while
 * the reporting stays in one place.
 *
 * <p>It extends {@link Exception} rather than {@code RuntimeException} so the
 * compiler forces every caller to either handle it or declare it, making it
 * impossible to forget one of these errors by accident.
 */
public class ChioneException extends Exception {
    /**
     * Creates an exception carrying a message meant for the user.
     *
     * @param message a clear explanation of what went wrong, and ideally how to fix it
     */
    public ChioneException(String message) {
        super(message); // Exception stores the message; getMessage() reads it back
    }
}
