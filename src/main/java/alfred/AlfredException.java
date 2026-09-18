package alfred;

/**
 * Signals a problem the program can tell the user about and carry on from.
 * The message is shown to the user word for word.
 */
public class AlfredException extends Exception {
    /**
     * Creates an exception carrying the message to show the user.
     *
     * @param message the explanation, phrased as Alfred would say it.
     */
    public AlfredException(String message) {
        super(message);
    }
}
