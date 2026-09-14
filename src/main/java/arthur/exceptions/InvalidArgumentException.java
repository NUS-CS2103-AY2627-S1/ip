package arthur.exceptions;

/**
 * Indicates that a command received an invalid argument.
 */
public class InvalidArgumentException extends ArthurRuntimeException {
    public InvalidArgumentException(String value) {
        super("An invalid argument was passed to the command: " + value);
    }

    public InvalidArgumentException(String value, String reason) {
        super("An invalid argument was passed to the command: " + value + " (" + reason + ")");
    }
}
