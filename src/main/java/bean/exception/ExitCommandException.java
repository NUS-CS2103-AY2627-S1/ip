package bean.exception;

/** Signals that the application should terminate after displaying its message. */
public class ExitCommandException extends RuntimeException {

    /** Creates an exit signal with the farewell message to display. */
    public ExitCommandException(String message) {
        super(message);
    }
}
