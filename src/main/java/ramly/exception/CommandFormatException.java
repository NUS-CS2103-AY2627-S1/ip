package ramly.exception;

/** Indicates that user input does not follow a supported command format. */
public class CommandFormatException extends RuntimeException {
    /** Creates an exception with an actionable user-facing message. */
    public CommandFormatException(String message) {
        super(message);
    }
}
