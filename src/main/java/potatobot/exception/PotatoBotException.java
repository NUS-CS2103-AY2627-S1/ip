package potatobot.exception;

/**
 * Represents an error that PotatoBot can report to the user.
 */
public class PotatoBotException extends Exception {
    /**
     * Creates an exception with a message suitable for display to the user.
     *
     * @param message Description of the error.
     */
    public PotatoBotException(String message) {
        super(message);
    }
}
