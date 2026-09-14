package nova;

/**
 * Signals that the chatbot cannot carry out what the user asked for.
 *
 * <p>The message is written for the user rather than for a log, because it is
 * shown to them verbatim after the "OOPS!!!" prefix. It is a checked exception
 * so that every place able to produce one has to say so.
 */
public class NovaException extends Exception {
    /**
     * Creates an exception carrying an explanation for the user.
     *
     * @param message what went wrong, phrased for the person at the keyboard
     */
    public NovaException(String message) {
        super(message);
    }
}
