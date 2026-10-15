package tungtung;

/**
 * Represents a command that Tung Tung cannot process because its format is invalid.
 */
public class TungTungException extends Exception {
    /**
     * Creates an exception with a message that can be shown to the user.
     *
     * @param message explanation of the invalid command
     */
    public TungTungException(String message) {
        super(message);
    }
}
