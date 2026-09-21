package ted;

/**
 * Signals that Ted cannot carry out what the user asked for.
 * <p>
 * This is a checked exception because every one of these is a problem the
 * conversation can recover from: Ted explains what went wrong and waits for the
 * next command. Carrying the explanation in the exception lets the code that
 * detects a problem describe it, while the main loop decides how to show it.
 */
public class TedException extends Exception {
    /** Identifies this exception class during Java serialization. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception carrying an explanation written for the user.
     *
     * @param message what went wrong, phrased as Ted would say it.
     */
    public TedException(String message) {
        super(message);
    }
}
