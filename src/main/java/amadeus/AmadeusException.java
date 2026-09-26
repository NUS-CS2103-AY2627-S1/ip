package amadeus;

/**
 * Signals that the chatbot could not carry out what the user asked.
 * <p>
 * Every message carried by this exception is written for the user to read, so a
 * caller can print {@link #getMessage()} directly instead of translating an
 * error code into words. It extends {@code Exception} rather than
 * {@code RuntimeException} so that the compiler forces each caller to decide
 * what to do about it, which is what keeps a bad command from ending the session.
 */
public class AmadeusException extends Exception {

    /** Written between the lines of a message: a line break, then the indent every reply uses. */
    private static final String LINE_SEPARATOR = "\n ";

    /**
     * Creates an exception carrying a message meant to be shown to the user.
     * <p>
     * Most messages are two lines - what went wrong, then how to say it correctly - so
     * the lines are taken separately and joined here. Passing them as one string with
     * a "\n " in the middle would work too, but every caller would then have to
     * remember the indent, and forgetting it misaligns only that one message.
     *
     * @param lines the message, one line per argument; a single line is the common case.
     */
    public AmadeusException(String... lines) {
        super(String.join(LINE_SEPARATOR, lines));
    }
}
