package echo.exception;

/**
 * A {@link EchoException} for when the command for creating a {@link echo.task.Event} does not match the
 * expected format.
 */
public class EventFormatException extends EchoException {
    /** Creates the error shown when an event command does not follow the expected format. */
    public EventFormatException() {
        super("I need a description and valid times. "
                + "Try: event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>\n"
                + "Example: event party /from 2019-12-02 1800 /to 2019-12-02 2100");
    }
}
