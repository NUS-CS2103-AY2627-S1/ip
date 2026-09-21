package echo.exception;

/**
 * Signals that the stats command was entered with unsupported arguments.
 */
public class StatsFormatException extends EchoException {
    /** Creates the error shown when a {@code stats} command has additional arguments */
    public StatsFormatException() {
        super("The stats command does not take extra details. Try: stats");
    }
}
