package billy;

/**
 * Signals that Billy could not carry out what the user asked for.
 *
 * <p>The message carried by this exception is written for the user to read, so it
 * should explain what went wrong and, where useful, how to type the command
 * correctly. Billy catches these in one place and prints the message, which keeps
 * the checks themselves free of printing code.
 */
public class BillyException extends Exception {

    /**
     * Creates an exception carrying an explanation meant for the user.
     *
     * @param message what went wrong, phrased for the person typing the command
     */
    public BillyException(String message) {
        super(message);
    }
}
