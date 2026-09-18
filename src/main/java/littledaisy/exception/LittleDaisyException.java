package littledaisy.exception;

/**
 * Signals that littleDaisy could not carry out what the user asked.
 *
 * <p>The message carried by this exception is written for the user to read,
 * not for a developer: it is printed as-is after the {@code OOPS!!!} prefix.
 * Anything thrown here is expected and recoverable -- the conversation carries
 * on with the next line of input.
 */
public class LittleDaisyException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception carrying a message meant for the user.
     *
     * @param message what went wrong, phrased for the user
     */
    public LittleDaisyException(String message) {
        super(message);
    }
}
