package benbot;

/** The base class for input errors that BenBot can report to the user. */
public class BenBotException extends Exception {
    /** The version identifier used when this exception is serialized. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a user-friendly explanation.
     *
     * @param message the explanation that BenBot should display.
     */
    public BenBotException(String message) {
        super(message);
    }
}
