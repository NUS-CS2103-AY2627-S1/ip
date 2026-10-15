package am.command;

/** Indicates that a command name is not supported by the application. */
public class UnknownCommandException extends RuntimeException {
    /** Creates an exception with a user-facing explanation. */
    public UnknownCommandException(String message) {
        super(message);
    }
}
