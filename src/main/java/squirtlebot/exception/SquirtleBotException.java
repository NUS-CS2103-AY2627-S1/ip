package squirtlebot.exception;

/**
 * Represents an exception specific to SquirtleBot operations.
 * Serves as the base class for more specific application exceptions, allowing
 * callers to handle all SquirtleBot-related failures through a common type.
 */
public class SquirtleBotException extends RuntimeException {
    /**
     * Constructs a SquirtleBotException with a message describing the failure.
     *
     * @param message description of the failure.
     */
    public SquirtleBotException(String message) {
        super(message);
    }

    /**
     * Constructs a SquirtleBotException with a message and its underlying cause.
     *
     * @param message description of the failure.
     * @param cause exception that caused this failure.
     */
    public SquirtleBotException(String message, Throwable cause) {
        super(message, cause);
    }
}
