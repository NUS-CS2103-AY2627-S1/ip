package hermes;

/**
 * Reports a problem Hermes can explain to the user and carry on from.
 *
 * <p>It is checked, so any method that can fail this way must say so, and the
 * conversation loop catches it and shows the message rather than stopping.
 */
public class HermesException extends Exception {
    /**
     * @param message The explanation to show the user.
     */
    public HermesException(String message) {
        super(message);
    }
}
