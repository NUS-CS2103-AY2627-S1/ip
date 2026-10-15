package squirtlebot.exception;

/**
 * Represents exceptions encountered during command execution, such as index out of bounds.
 */
public class CommandException extends SquirtleBotException {

    /**
     * Constructs a CommandException with a message describing the error encountered.
     *
     * @param message description of error encountered.
     */
    public CommandException(String message) {
        super(message);
    }

    /**
     * Constructs a CommandException with a message and underlying cause of failure.
     *
     * @param message description of error encountered.
     * @param cause underlying cause of failure.
     */
    public CommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
