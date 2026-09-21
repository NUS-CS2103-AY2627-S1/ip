package echo.exception;

/**
 * An {@link EchoException} for when the input command is unrecognized.
 */
public class UnknownCommandException extends EchoException {
    /** Creates the error shown when the input command is unrecognized. */
    public UnknownCommandException() {
        super("I don't recognise that transmission. "
                + "Try todo, deadline, event, list, find, mark, unmark, delete, stats, or bye.");
    }
}
