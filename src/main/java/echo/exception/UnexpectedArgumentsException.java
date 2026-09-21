package echo.exception;

/**
 * Signals that an argument-free command was given extra details.
 */
public class UnexpectedArgumentsException extends EchoException {
    /**
     * Creates an error naming the command that received unexpected arguments.
     *
     * @param keyword Command keyword.
     */
    public UnexpectedArgumentsException(String keyword) {
        super("The " + keyword + " command does not take extra details. Try: " + keyword);
    }
}
